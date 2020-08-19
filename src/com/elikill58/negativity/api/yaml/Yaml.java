package com.elikill58.negativity.api.yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.elikill58.negativity.api.yaml.composer.Composer;
import com.elikill58.negativity.api.yaml.constructor.BaseConstructor;
import com.elikill58.negativity.api.yaml.constructor.Constructor;
import com.elikill58.negativity.api.yaml.emitter.Emitable;
import com.elikill58.negativity.api.yaml.emitter.Emitter;
import com.elikill58.negativity.api.yaml.error.YAMLException;
import com.elikill58.negativity.api.yaml.events.Event;
import com.elikill58.negativity.api.yaml.introspector.BeanAccess;
import com.elikill58.negativity.api.yaml.nodes.Node;
import com.elikill58.negativity.api.yaml.nodes.Tag;
import com.elikill58.negativity.api.yaml.parser.Parser;
import com.elikill58.negativity.api.yaml.parser.ParserImpl;
import com.elikill58.negativity.api.yaml.reader.StreamReader;
import com.elikill58.negativity.api.yaml.reader.UnicodeReader;
import com.elikill58.negativity.api.yaml.representer.Representer;
import com.elikill58.negativity.api.yaml.resolver.Resolver;
import com.elikill58.negativity.api.yaml.serializer.Serializer;

@SuppressWarnings("unchecked")
public class Yaml {
	protected final Resolver resolver;
	private String name;
	protected BaseConstructor constructor;
	protected Representer representer;
	protected DumperOptions dumperOptions;

	public Yaml() {
		this(new Constructor(), new Representer(), new DumperOptions(), new LoaderOptions(), new Resolver());
	}

	public Yaml(final BaseConstructor constructor, final Representer representer) {
		this(constructor, representer, initDumperOptions(representer));
	}

	private static DumperOptions initDumperOptions(final Representer representer) {
		final DumperOptions dumperOptions = new DumperOptions();
		dumperOptions.setDefaultFlowStyle(representer.getDefaultFlowStyle());
		dumperOptions.setDefaultScalarStyle(representer.getDefaultScalarStyle());
		dumperOptions.setAllowReadOnlyProperties(representer.getPropertyUtils().isAllowReadOnlyProperties());
		dumperOptions.setTimeZone(representer.getTimeZone());
		return dumperOptions;
	}

	public Yaml(final Representer representer, final DumperOptions dumperOptions) {
		this(new Constructor(), representer, dumperOptions, new LoaderOptions(), new Resolver());
	}

	public Yaml(final BaseConstructor constructor, final Representer representer, final DumperOptions dumperOptions) {
		this(constructor, representer, dumperOptions, new LoaderOptions(), new Resolver());
	}

	public Yaml(final BaseConstructor constructor, final Representer representer, final DumperOptions dumperOptions,
			final LoaderOptions loadingConfig) {
		this(constructor, representer, dumperOptions, loadingConfig, new Resolver());
	}

	public Yaml(final BaseConstructor constructor, final Representer representer, final DumperOptions dumperOptions,
			final Resolver resolver) {
		this(constructor, representer, dumperOptions, new LoaderOptions(), resolver);
	}

	public Yaml(final BaseConstructor constructor, final Representer representer, final DumperOptions dumperOptions,
			final LoaderOptions loadingConfig, final Resolver resolver) {
		if (!constructor.isExplicitPropertyUtils()) {
			constructor.setPropertyUtils(representer.getPropertyUtils());
		} else if (!representer.isExplicitPropertyUtils()) {
			representer.setPropertyUtils(constructor.getPropertyUtils());
		}
		(this.constructor = constructor).setAllowDuplicateKeys(loadingConfig.isAllowDuplicateKeys());
		if (dumperOptions.getIndent() <= dumperOptions.getIndicatorIndent()) {
			throw new YAMLException("Indicator indent must be smaller then indent.");
		}
		representer.setDefaultFlowStyle(dumperOptions.getDefaultFlowStyle());
		representer.setDefaultScalarStyle(dumperOptions.getDefaultScalarStyle());
		representer.getPropertyUtils().setAllowReadOnlyProperties(dumperOptions.isAllowReadOnlyProperties());
		representer.setTimeZone(dumperOptions.getTimeZone());
		this.representer = representer;
		this.dumperOptions = dumperOptions;
		this.resolver = resolver;
		this.name = "Yaml:" + System.identityHashCode(this);
	}

	public String dumpAll(final Iterator<?> data) {
		final StringWriter buffer = new StringWriter();
		this.dumpAll(data, buffer, null);
		return buffer.toString();
	}

	public void dump(final Object data, final Writer output) {
		final List<Object> list = new ArrayList<Object>(1);
		list.add(data);
		this.dumpAll(list.iterator(), output, null);
	}

	private void dumpAll(final Iterator<?> data, final Writer output, final Tag rootTag) {
		final Serializer serializer = new Serializer(new Emitter(output, this.dumperOptions), this.resolver,
				this.dumperOptions, rootTag);
		try {
			serializer.open();
			while (data.hasNext()) {
				final Node node = this.representer.represent(data.next());
				serializer.serialize(node);
			}
			serializer.close();
		} catch (IOException e) {
			throw new YAMLException(e);
		}
	}

	public String dumpAs(final Object data, final Tag rootTag, final DumperOptions.FlowStyle flowStyle) {
		final DumperOptions.FlowStyle oldStyle = this.representer.getDefaultFlowStyle();
		if (flowStyle != null) {
			this.representer.setDefaultFlowStyle(flowStyle);
		}
		final List<Object> list = new ArrayList<Object>(1);
		list.add(data);
		final StringWriter buffer = new StringWriter();
		this.dumpAll(list.iterator(), buffer, rootTag);
		this.representer.setDefaultFlowStyle(oldStyle);
		return buffer.toString();
	}

	public List<Event> serialize(final Node data) {
		final SilentEmitter emitter = new SilentEmitter();
		final Serializer serializer = new Serializer(emitter, this.resolver, this.dumperOptions, null);
		try {
			serializer.open();
			serializer.serialize(data);
			serializer.close();
		} catch (IOException e) {
			throw new YAMLException(e);
		}
		return emitter.getEvents();
	}

	public <T> T load(final String yaml) {
		return (T) this.loadFromReader(new StreamReader(yaml), Object.class);
	}

	public <T> T load(final InputStream io) {
		return (T) this.loadFromReader(new StreamReader(new UnicodeReader(io)), Object.class);
	}

	public <T> T loadAs(final Reader io, final Class<T> type) {
		return (T) this.loadFromReader(new StreamReader(io), type);
	}

	private Object loadFromReader(final StreamReader sreader, final Class<?> type) {
		final Composer composer = new Composer(new ParserImpl(sreader), this.resolver);
		this.constructor.setComposer(composer);
		return this.constructor.getSingleData(type);
	}

	public Iterable<Object> loadAll(final Reader yaml) {
		final Composer composer = new Composer(new ParserImpl(new StreamReader(yaml)), this.resolver);
		this.constructor.setComposer(composer);
		final Iterator<Object> result = new Iterator<Object>() {
			@Override
			public boolean hasNext() {
				return Yaml.this.constructor.checkData();
			}

			@Override
			public Object next() {
				return Yaml.this.constructor.getData();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
		return new YamlIterable(result);
	}
	
	public Iterable<Node> composeAll(final Reader yaml) {
		final Composer composer = new Composer(new ParserImpl(new StreamReader(yaml)), this.resolver);
		this.constructor.setComposer(composer);
		final Iterator<Node> result = new Iterator<Node>() {
			@Override
			public boolean hasNext() {
				return composer.checkNode();
			}

			@Override
			public Node next() {
				return composer.getNode();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
		return new NodeIterable(result);
	}

	@Override
	public String toString() {
		return this.name;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Iterable<Event> parse(final Reader yaml) {
		final Parser parser = new ParserImpl(new StreamReader(yaml));
		final Iterator<Event> result = new Iterator<Event>() {
			@Override
			public boolean hasNext() {
				return parser.peekEvent() != null;
			}

			@Override
			public Event next() {
				return parser.getEvent();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
		return new EventIterable(result);
	}

	public void setBeanAccess(final BeanAccess beanAccess) {
		this.constructor.getPropertyUtils().setBeanAccess(beanAccess);
		this.representer.getPropertyUtils().setBeanAccess(beanAccess);
	}

	public void addTypeDescription(final TypeDescription td) {
		this.constructor.addTypeDescription(td);
		this.representer.addTypeDescription(td);
	}

	private static class SilentEmitter implements Emitable {
		private List<Event> events;

		private SilentEmitter() {
			this.events = new ArrayList<Event>(100);
		}

		public List<Event> getEvents() {
			return this.events;
		}

		@Override
		public void emit(final Event event) throws IOException {
			this.events.add(event);
		}
	}

	private static class YamlIterable implements Iterable<Object> {
		private Iterator<Object> iterator;

		public YamlIterable(final Iterator<Object> iterator) {
			this.iterator = iterator;
		}

		@Override
		public Iterator<Object> iterator() {
			return this.iterator;
		}
	}

	private static class NodeIterable implements Iterable<Node> {
		private Iterator<Node> iterator;

		public NodeIterable(final Iterator<Node> iterator) {
			this.iterator = iterator;
		}

		@Override
		public Iterator<Node> iterator() {
			return this.iterator;
		}
	}

	private static class EventIterable implements Iterable<Event> {
		private Iterator<Event> iterator;

		public EventIterable(final Iterator<Event> iterator) {
			this.iterator = iterator;
		}

		@Override
		public Iterator<Event> iterator() {
			return this.iterator;
		}
	}
}
