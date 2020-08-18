package com.elikill58.negativity.api.yaml.serializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elikill58.negativity.api.yaml.DumperOptions;
import com.elikill58.negativity.api.yaml.emitter.Emitable;
import com.elikill58.negativity.api.yaml.events.AliasEvent;
import com.elikill58.negativity.api.yaml.events.DocumentEndEvent;
import com.elikill58.negativity.api.yaml.events.DocumentStartEvent;
import com.elikill58.negativity.api.yaml.events.ImplicitTuple;
import com.elikill58.negativity.api.yaml.events.MappingEndEvent;
import com.elikill58.negativity.api.yaml.events.MappingStartEvent;
import com.elikill58.negativity.api.yaml.events.ScalarEvent;
import com.elikill58.negativity.api.yaml.events.SequenceEndEvent;
import com.elikill58.negativity.api.yaml.events.SequenceStartEvent;
import com.elikill58.negativity.api.yaml.events.StreamEndEvent;
import com.elikill58.negativity.api.yaml.events.StreamStartEvent;
import com.elikill58.negativity.api.yaml.nodes.AnchorNode;
import com.elikill58.negativity.api.yaml.nodes.CollectionNode;
import com.elikill58.negativity.api.yaml.nodes.MappingNode;
import com.elikill58.negativity.api.yaml.nodes.Node;
import com.elikill58.negativity.api.yaml.nodes.NodeId;
import com.elikill58.negativity.api.yaml.nodes.NodeTuple;
import com.elikill58.negativity.api.yaml.nodes.ScalarNode;
import com.elikill58.negativity.api.yaml.nodes.SequenceNode;
import com.elikill58.negativity.api.yaml.nodes.Tag;
import com.elikill58.negativity.api.yaml.resolver.Resolver;

public final class Serializer {
	private final Emitable emitter;
	private final Resolver resolver;
	private boolean explicitStart;
	private boolean explicitEnd;
	private DumperOptions.Version useVersion;
	private Map<String, String> useTags;
	private Set<Node> serializedNodes;
	private Map<Node, String> anchors;
	private AnchorGenerator anchorGenerator;
	private Boolean closed;
	private Tag explicitRoot;

	public Serializer(final Emitable emitter, final Resolver resolver, final DumperOptions opts, final Tag rootTag) {
		this.emitter = emitter;
		this.resolver = resolver;
		this.explicitStart = opts.isExplicitStart();
		this.explicitEnd = opts.isExplicitEnd();
		if (opts.getVersion() != null) {
			this.useVersion = opts.getVersion();
		}
		this.useTags = opts.getTags();
		this.serializedNodes = new HashSet<Node>();
		this.anchors = new HashMap<Node, String>();
		this.anchorGenerator = opts.getAnchorGenerator();
		this.closed = null;
		this.explicitRoot = rootTag;
	}

	public void open() throws IOException {
		if (this.closed == null) {
			this.emitter.emit(new StreamStartEvent(null, null));
			this.closed = Boolean.FALSE;
			return;
		}
		if (Boolean.TRUE.equals(this.closed)) {
			throw new SerializerException("serializer is closed");
		}
		throw new SerializerException("serializer is already opened");
	}

	public void close() throws IOException {
		if (this.closed == null) {
			throw new SerializerException("serializer is not opened");
		}
		if (!Boolean.TRUE.equals(this.closed)) {
			this.emitter.emit(new StreamEndEvent(null, null));
			this.closed = Boolean.TRUE;
		}
	}

	public void serialize(final Node node) throws IOException {
		if (this.closed == null) {
			throw new SerializerException("serializer is not opened");
		}
		if (this.closed) {
			throw new SerializerException("serializer is closed");
		}
		this.emitter.emit(new DocumentStartEvent(null, null, this.explicitStart, this.useVersion, this.useTags));
		this.anchorNode(node);
		if (this.explicitRoot != null) {
			node.setTag(this.explicitRoot);
		}
		this.serializeNode(node, null);
		this.emitter.emit(new DocumentEndEvent(null, null, this.explicitEnd));
		this.serializedNodes.clear();
		this.anchors.clear();
	}

	private void anchorNode(Node node) {
		if (node.getNodeId() == NodeId.anchor) {
			node = ((AnchorNode) node).getRealNode();
		}
		if (this.anchors.containsKey(node)) {
			String anchor = this.anchors.get(node);
			if (null == anchor) {
				anchor = this.anchorGenerator.nextAnchor(node);
				this.anchors.put(node, anchor);
			}
		} else {
			this.anchors.put(node, null);
			switch (node.getNodeId()) {
			case sequence: {
				final SequenceNode seqNode = (SequenceNode) node;
				final List<Node> list = seqNode.getValue();
				for (final Node item : list) {
					this.anchorNode(item);
				}
				break;
			}
			case mapping: {
				final MappingNode mnode = (MappingNode) node;
				final List<NodeTuple> map = mnode.getValue();
				for (final NodeTuple object : map) {
					final Node key = object.getKeyNode();
					final Node value = object.getValueNode();
					this.anchorNode(key);
					this.anchorNode(value);
				}
				break;
			}
			default:
				break;
			}
		}
	}

	private void serializeNode(Node node, final Node parent) throws IOException {
		if (node.getNodeId() == NodeId.anchor) {
			node = ((AnchorNode) node).getRealNode();
		}
		final String tAlias = this.anchors.get(node);
		if (this.serializedNodes.contains(node)) {
			this.emitter.emit(new AliasEvent(tAlias, null, null));
		} else {
			this.serializedNodes.add(node);
			switch (node.getNodeId()) {
			case scalar: {
				final ScalarNode scalarNode = (ScalarNode) node;
				final Tag detectedTag = this.resolver.resolve(NodeId.scalar, scalarNode.getValue(), true);
				final Tag defaultTag = this.resolver.resolve(NodeId.scalar, scalarNode.getValue(), false);
				final ImplicitTuple tuple = new ImplicitTuple(node.getTag().equals(detectedTag),
						node.getTag().equals(defaultTag));
				final ScalarEvent event = new ScalarEvent(tAlias, node.getTag().getValue(), tuple,
						scalarNode.getValue(), null, null, scalarNode.getStyle());
				this.emitter.emit(event);
				break;
			}
			case sequence: {
				final SequenceNode seqNode = (SequenceNode) node;
				final boolean implicitS = node.getTag().equals(this.resolver.resolve(NodeId.sequence, null, true));
				this.emitter.emit(new SequenceStartEvent(tAlias, node.getTag().getValue(), implicitS, null, null,
						seqNode.getFlowStyle()));
				final List<Node> list = seqNode.getValue();
				for (final Node item : list) {
					this.serializeNode(item, node);
				}
				this.emitter.emit(new SequenceEndEvent(null, null));
				break;
			}
			default: {
				final Tag implicitTag = this.resolver.resolve(NodeId.mapping, null, true);
				final boolean implicitM = node.getTag().equals(implicitTag);
				this.emitter.emit(new MappingStartEvent(tAlias, node.getTag().getValue(), implicitM, null, null,
						((CollectionNode<?>) node).getFlowStyle()));
				final MappingNode mnode = (MappingNode) node;
				final List<NodeTuple> map = mnode.getValue();
				for (final NodeTuple row : map) {
					final Node key = row.getKeyNode();
					final Node value = row.getValueNode();
					this.serializeNode(key, mnode);
					this.serializeNode(value, mnode);
				}
				this.emitter.emit(new MappingEndEvent(null, null));
				break;
			}
			}
		}
	}
}
