package com.elikill58.negativity.universal.webhooks.integrations;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.net.ssl.HttpsURLConnection;

import com.elikill58.negativity.api.Content;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.json.JSONObject;
import com.elikill58.negativity.api.json.parser.JSONParser;
import com.elikill58.negativity.api.yaml.Configuration;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Tuple;
import com.elikill58.negativity.universal.webhooks.Webhook;
import com.elikill58.negativity.universal.webhooks.integrations.DiscordWebhook.DiscordWebhookRequest.EmbedObject;
import com.elikill58.negativity.universal.webhooks.messages.WebhookMessage;
import com.elikill58.negativity.universal.webhooks.messages.WebhookMessage.WebhookMessageType;

public class DiscordWebhook implements Webhook {
    
	private final Configuration config;
	private final String webhookUrl;
	private final ScheduledExecutorService executorService;
	private final List<UUID> alreadySent = new ArrayList<>();
	private final List<WebhookMessage> queue = new ArrayList<>();
	private long time = 0, cooldown = 0;
	private final Content<Long> players = new Content<>();
	
	public DiscordWebhook(Configuration config) {
		this.config = config;
		this.cooldown = config.getLong("cooldown", 0);
		this.webhookUrl = config.getString("url");
		this.executorService = Executors.newScheduledThreadPool(1);
	}
	
	private long getTheoricCooldown(WebhookMessageType type) {
		return config.getLong("messages." + type.name().toLowerCase(Locale.ROOT) + ".cooldown", this.cooldown);
	}

	public long getCooldown(Player p, WebhookMessageType type) {
		long saved = players.get(type, p.getUniqueId().toString(), System.currentTimeMillis() + getTheoricCooldown(type));
		return saved == 0 ? 0 : System.currentTimeMillis() - saved;
	}

	public boolean hasCooldown(Player p, WebhookMessageType type) {
		return getCooldown(p, type) < 0;
	}
	
	public void setCooldown(Player p, WebhookMessageType type) {
		players.set(type, p.getUniqueId().toString(), System.currentTimeMillis() + getTheoricCooldown(type));
	}
	
	@Override
	public void close() {
		if(!executorService.isShutdown())
			executorService.shutdown();
	}
	
    @Override
    public String getWebhookName() {
    	return "Discord";
    }
    
    @Override
    public void clean(Player p) {
    	for(WebhookMessageType type : WebhookMessageType.values())
    		players.remove(type, p.getUniqueId().toString());
    	alreadySent.remove(p.getUniqueId());
    }
    
    @Override
    public void addToQueue(WebhookMessage msg) {
    	if(msg == null)
    		return;
    	if(!msg.canCombine())
    		send(msg);
    	else {
    		synchronized (queue) {
        		queue.add(msg);
        		for(WebhookMessage other : new ArrayList<>(queue)) { // for all queued messages
        			WebhookMessage combined = msg.combine(other); // try to combine
        			if(combined != null) { // can combine, remove old and add new
        				queue.remove(other); // remove old old
        				queue.remove(msg);
        				queue.add(combined); // add new combined
        				break; // found combine
        			}
        		}
			}
    	}
    }
    
    @Override
    public void runQueue() {
    	if(time > System.currentTimeMillis()) { // should skip
    		return;
    	}
    	
    	// firstly, combine all
		queue.removeIf(Objects::isNull);
		if(queue.isEmpty())
			return;
		queue.sort(Comparator.naturalOrder());
		while(!queue.isEmpty()) {
    		WebhookMessage msg = queue.remove(0);
    		if(queue.isEmpty()) { // removed is last
    			send(msg);
    		} else if(queue.size() == 1) { // stay only one other
    			WebhookMessage other = queue.remove(0);
    			WebhookMessage third = msg.combine(other); // try to combine
    			if(third != null) // success, then new one
    				send(third);
    			else { // failed, send 2 last
    				send(msg);
    				send(other);
    			}
    		} else {
    			List<WebhookMessage> toRemove = new ArrayList<>();
				for(int i = 0; i < queue.size(); i++) {
					if(queue.size() <= i)
						break;
					WebhookMessage other = queue.get(i);
					WebhookMessage third = msg.combine(other);
					if(third != null) {
						msg = third;
						toRemove.add(other);
					}
				}
				queue.removeAll(toRemove);
				send(msg);
    		}
		}
    }
    
    @Override
    public void send(WebhookMessage msg) {
    	try {
    		executorService.execute(() -> sendAsync(msg));
    	} catch (Exception e) {
    		e.printStackTrace();
		}
    }
    
    private void sendAsync(WebhookMessage msg) {
    	try {
    		sendAsyncWithException(msg);
    	} catch (Exception e) {
    		Adapter.getAdapter().getLogger().printError("Error while sending webhook message about " + msg.getConcerned().getName(), e);
		}
    }
    
    private void sendAsyncWithException(WebhookMessage msg) throws Exception {
    	queue.remove(msg);
    	Configuration confMsg = config.getSection("messages." + msg.getMessageType().name().toLowerCase(Locale.ROOT));
    	if(confMsg == null) // not config
    		return;
    	Adapter ada = Adapter.getAdapter();
    	if(!confMsg.getBoolean("enabled", true)) {
        	ada.debug("Webhook for " + msg.getMessageType().name() + " is not enabled.");
    		return;
    	}
    	// if offline, don't care about cooldown
    	if(time > System.currentTimeMillis() || (msg.getConcerned().isOnline() && hasCooldown(msg.getConcerned(), msg.getMessageType()))) { // should skip
    		queue.add(msg);
        	ada.debug("Skipping " + msg.getMessageType().name() + ": " + (time > System.currentTimeMillis() ? "waiting for discord" : "player cooldown: " + getCooldown(msg.getConcerned(), msg.getMessageType())));
    		return;
    	}
    	ada.debug("Sending webhook " + msg.getMessageType().name() + " for " + msg.getConcerned().getName() + ": " + getCooldown(msg.getConcerned(), msg.getMessageType()));
    	setCooldown(msg.getConcerned(), msg.getMessageType());
    	queue.removeIf(w -> w.getDate() == msg.getDate()); // be sure it's removed
    	DiscordWebhookRequest webhook = new DiscordWebhookRequest(webhookUrl);
	    webhook.setUsername(msg.applyPlaceHolders(confMsg.getString("username", "Negativity")));
	    if(alreadySent.contains(msg.getConcerned().getUniqueId())) { // if already sent first message
		    webhook.setContent(msg.applyPlaceHolders(confMsg.getString("content", "")));
	    } else { // is first message
	    	alreadySent.add(msg.getConcerned().getUniqueId());
		    webhook.setContent(msg.applyPlaceHolders(confMsg.getString("content_first", confMsg.getString("content", ""))));
	    }
	    webhook.setAvatarUrl(msg.applyPlaceHolders(confMsg.getString("avatar_url", "https://www.spigotmc.org/data/resource_icons/86/86874.jpg")));
	    Configuration embed = confMsg.getSection("embed");
	    if(embed != null) {
		    EmbedObject obj = new DiscordWebhookRequest.EmbedObject();
		    obj.setColor(Color.getColor(embed.getString("color", "red")));
		    obj.setTitle(msg.applyPlaceHolders(embed.getString("title", msg.getMessageType().name().toLowerCase(Locale.ROOT))));
		    obj.setDescription(msg.applyPlaceHolders(embed.getString("description", "")));

		    Configuration fields = embed.getSection("fields");
		    if(fields != null) {
		    	fields.getKeys().forEach((key) -> {
		    		Configuration fieldConfig = fields.getSection(key);
		    		obj.addField(msg.applyPlaceHolders(fieldConfig.getString("key", "")),
		    				msg.applyPlaceHolders(fieldConfig.getString("value", "")),
		    				fieldConfig.getBoolean("inline", true));
		    	});
		    }
		    
		    obj.setThumbnail(msg.applyPlaceHolders(embed.getString("thumbnail", "https://www.spigotmc.org/data/resource_icons/86/86874.jpg")));
		    Configuration footer = embed.getSection("footer");
		    if(footer != null) {
		    	obj.setFooter(msg.applyPlaceHolders(footer.getString("name", "Negativity - %date%")), footer.getString("link", "https://www.spigotmc.org/data/resource_icons/86/86874.jpg"));
		    }
		    Configuration author = embed.getSection("author");
		    if(author != null) {
		    	obj.setAuthor(msg.applyPlaceHolders(author.getString("name", "Negativity")),
		    			msg.applyPlaceHolders(author.getString("link", "https://github.com/Elikill58/Negativity")),
		    			msg.applyPlaceHolders(author.getString("icon", "https://www.spigotmc.org/data/resource_icons/86/86874.jpg")));
		    }

		    webhook.addEmbed(obj);
		    ada.debug("Added embed");
	    } else
		    ada.debug("No embed to add.");
		Tuple<Integer, String> webhookResult = webhook.execute();
	    ada.debug("Webhook result: " + webhookResult.toString());
		int code = webhookResult.getA();
		if(code < 200 || code >= 300) {
	    	if(code == 429) { // good config and error while sending
	    		JSONObject json = (JSONObject) new JSONParser().parse(webhookResult.getB());
	    		queue.add(msg);
	    		long retryAfter = (long) json.get("retry_after");
	    		time = System.currentTimeMillis() + retryAfter; // wait 5 s until next send
	    		ada.getLogger().warn("Discord webhook reach rate limit. Wait " + (retryAfter / 1000) + " secs more.");
	    	} else
	    		ada.getLogger().warn("Error while trying to send webhook request (code: " + code + "): " + webhookResult.getB());
		}
    }
    
    @Override
    public boolean ping(String asker) {
    	DiscordWebhookRequest webhook = new DiscordWebhookRequest(webhookUrl);
		webhook.setContent("");
	    webhook.setAvatarUrl("https://www.spigotmc.org/data/resource_icons/86/86874.jpg");
	    webhook.setUsername("Negativity Test");
	    webhook.addEmbed(new DiscordWebhookRequest.EmbedObject()
            .setColor(Color.GREEN)
            .setTitle("Test")
            .setDescription(asker + " try to ping webhook.")
		    .addField("Player name", asker, true)
		    .setThumbnail("https://www.spigotmc.org/data/resource_icons/86/86874.jpg")
		    .setFooter("Negativity - " + new Timestamp(System.currentTimeMillis()).toString().split("\\.", 2)[0], "https://www.spigotmc.org/data/resource_icons/86/86874.jpg")
		    .setAuthor("Negativity", "https://github.com/Elikill58/Negativity", "https://www.spigotmc.org/data/resource_icons/86/86874.jpg")
		    .setUrl("https://github.com/Elikill58/Negativity"));
	    try {
			webhook.execute();
		    return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
    }

	/**
	 * Class used to execute Discord Webhooks
	 * 
	 * @author k3kdude
	 * <p>
	 * Source: https://gist.github.com/k3kdude/fba6f6b37594eae3d6f9475330733bdb
	 */
    public static class DiscordWebhookRequest {

        private final String url;
        private String content;
        private String username;
        private String avatarUrl;
        private boolean tts;
        private List<EmbedObject> embeds = new ArrayList<>();

        /**
         * Constructs a new DiscordWebhook instance
         *
         * @param url The webhook URL obtained in Discord
         */
        public DiscordWebhookRequest(String url) {
            this.url = url;
        }

	    public void setContent(String content) {
	        this.content = content;
	    }
	
	    public void setUsername(String username) {
	        this.username = username;
	    }
	
	    public void setAvatarUrl(String avatarUrl) {
	        this.avatarUrl = avatarUrl;
	    }
	
	    public void setTts(boolean tts) {
	        this.tts = tts;
	    }
	
	    public void addEmbed(EmbedObject embed) {
	        this.embeds.add(embed);
	    }
	
		@SuppressWarnings("unchecked")
		public Tuple<Integer, String> execute() throws IOException {
	        if (this.content == null && this.embeds.isEmpty()) {
	            throw new IllegalArgumentException("Set content or add at least one EmbedObject");
	        }
	
	        JSONObject json = new JSONObject();
	
	        json.put("content", this.content);
	        json.put("username", this.username);
	        json.put("avatar_url", this.avatarUrl);
	        json.put("tts", this.tts);
	
	        if (!this.embeds.isEmpty()) {
	            List<JSONObject> embedObjects = new ArrayList<>();
	
	            for (EmbedObject embed : this.embeds) {
	                JSONObject jsonEmbed = new JSONObject();
	
	                jsonEmbed.put("title", embed.getTitle());
	                jsonEmbed.put("description", embed.getDescription());
	                jsonEmbed.put("url", embed.getUrl());
	
	                if (embed.getColor() != null) {
	                    Color color = embed.getColor();
	                    int rgb = color.getRed();
	                    rgb = (rgb << 8) + color.getGreen();
	                    rgb = (rgb << 8) + color.getBlue();
	
	                    jsonEmbed.put("color", rgb);
	                }
	
	                EmbedObject.Footer footer = embed.getFooter();
	                EmbedObject.Image image = embed.getImage();
	                EmbedObject.Thumbnail thumbnail = embed.getThumbnail();
	                EmbedObject.Author author = embed.getAuthor();
	                List<EmbedObject.Field> fields = embed.getFields();
	
	                if (footer != null) {
	                    JSONObject jsonFooter = new JSONObject();
	
	                    jsonFooter.put("text", footer.getText());
	                    jsonFooter.put("icon_url", footer.getIconUrl());
	                    jsonEmbed.put("footer", jsonFooter);
	                }
	
	                if (image != null) {
	                    JSONObject jsonImage = new JSONObject();
	
	                    jsonImage.put("url", image.getUrl());
	                    jsonEmbed.put("image", jsonImage);
	                }
	
	                if (thumbnail != null) {
	                    JSONObject jsonThumbnail = new JSONObject();
	
	                    jsonThumbnail.put("url", thumbnail.getUrl());
	                    jsonEmbed.put("thumbnail", jsonThumbnail);
	                }
	
	                if (author != null) {
	                    JSONObject jsonAuthor = new JSONObject();
	
	                    jsonAuthor.put("name", author.getName());
	                    jsonAuthor.put("url", author.getUrl());
	                    jsonAuthor.put("icon_url", author.getIconUrl());
	                    jsonEmbed.put("author", jsonAuthor);
	                }
	
	                List<JSONObject> jsonFields = new ArrayList<>();
	                for (EmbedObject.Field field : fields) {
	                    JSONObject jsonField = new JSONObject();
	
	                    jsonField.put("name", field.getName());
	                    jsonField.put("value", field.getValue());
	                    jsonField.put("inline", field.isInline());
	
	                    jsonFields.add(jsonField);
	                }
	
	                jsonEmbed.put("fields", jsonFields.toArray());
	                embedObjects.add(jsonEmbed);
	            }
	
	            json.put("embeds", embedObjects.toArray());
	        }
	
	        URL url = new URL(this.url);
	        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
	        connection.addRequestProperty("Content-Type", "application/json");
	        connection.addRequestProperty("User-Agent", "Java-Negativity-DiscordWebhook-BY-Gelox_");
	        connection.setDoOutput(true);
	        connection.setRequestMethod("POST");
	        OutputStream stream = connection.getOutputStream();
	        stream.write(json.toString().getBytes());
	        stream.flush();
	        stream.close();
			String result = "";
			int code = connection.getResponseCode();
			if(code > 299 || code < 200) { // not successfull
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
				String inputLine;

				while ((inputLine = in.readLine()) != null)
					result += inputLine;
				in.close();
			} else { // done
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String inputLine;
		
				while ((inputLine = in.readLine()) != null)
					result += inputLine;
				in.close();
			}
	        connection.disconnect();
	        return new Tuple<Integer, String>(code, result);
	    }
	
	    public static class EmbedObject {
	        private String title;
	        private String description;
	        private String url;
	        private Color color;
	
	        private Footer footer;
	        private Thumbnail thumbnail;
	        private Image image;
	        private Author author;
	        private List<Field> fields = new ArrayList<>();
	        
	        public String getTitle() {
	            return title;
	        }
	
	        public String getDescription() {
	            return description;
	        }
	
	        public String getUrl() {
	            return url;
	        }
	
	        public Color getColor() {
	            return color;
	        }
	
	        public Footer getFooter() {
	            return footer;
	        }
	
	        public Thumbnail getThumbnail() {
	            return thumbnail;
	        }
	
	        public Image getImage() {
	            return image;
	        }
	
	        public Author getAuthor() {
	            return author;
	        }
	
	        public List<Field> getFields() {
	            return fields;
	        }
	
	        public EmbedObject setTitle(String title) {
	            this.title = title;
	            return this;
	        }
	
	        public EmbedObject setDescription(String description) {
	            this.description = description;
	            return this;
	        }
	
	        public EmbedObject setUrl(String url) {
	            this.url = url;
	            return this;
	        }
	
	        public EmbedObject setColor(Color color) {
	            this.color = color;
	            return this;
	        }
	
	        public EmbedObject setFooter(String text, String icon) {
	            this.footer = new Footer(text, icon);
	            return this;
	        }
	
	        public EmbedObject setThumbnail(String url) {
	            this.thumbnail = new Thumbnail(url);
	            return this;
	        }
	
	        public EmbedObject setImage(String url) {
	            this.image = new Image(url);
	            return this;
	        }
	
	        public EmbedObject setAuthor(String name, String url, String icon) {
	            this.author = new Author(name, url, icon);
	            return this;
	        }
	
	        public EmbedObject addField(String name, String value, boolean inline) {
	            this.fields.add(new Field(name, value, inline));
	            return this;
	        }
	
	        private class Footer {
	            private String text;
	            private String iconUrl;
	
	            private Footer(String text, String iconUrl) {
	                this.text = text;
	                this.iconUrl = iconUrl;
	            }
	
	            private String getText() {
	                return text;
	            }
	
	            private String getIconUrl() {
	                return iconUrl;
	            }
	        }
	
	        private class Thumbnail {
	            private String url;
	
	            private Thumbnail(String url) {
	                this.url = url;
	            }
	
	            private String getUrl() {
	                return url;
	            }
	        }
	
	        private class Image {
	            private String url;
	
	            private Image(String url) {
	                this.url = url;
	            }
	
	            private String getUrl() {
	                return url;
	            }
	        }
	
	        private class Author {
	            private String name;
	            private String url;
	            private String iconUrl;
	
	            private Author(String name, String url, String iconUrl) {
	                this.name = name;
	                this.url = url;
	                this.iconUrl = iconUrl;
	            }
	
	            private String getName() {
	                return name;
	            }
	
	            private String getUrl() {
	                return url;
	            }
	
	            private String getIconUrl() {
	                return iconUrl;
	            }
	        }
	
	        private class Field {
	            private String name;
	            private String value;
	            private boolean inline;
	
	            private Field(String name, String value, boolean inline) {
	                this.name = name;
	                this.value = value;
	                this.inline = inline;
	            }
	
	            private String getName() {
	                return name;
	            }
	
	            private String getValue() {
	                return value;
	            }
	
	            private boolean isInline() {
	                return inline;
	            }
	        }
	    }

    }
}
