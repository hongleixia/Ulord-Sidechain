/*
 * This file is part of USC
 * Copyright (C) 2016 - 2018 USC developer team.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package co.usc.net.simples;

import co.usc.core.BlockDifficulty;
import co.usc.net.*;
import co.usc.net.messages.Message;
import co.usc.net.messages.StatusMessage;
import co.usc.validators.DummyBlockValidationRule;
import co.usc.net.MessageHandler;
import co.usc.net.NodeID;
import co.usc.net.NodeMessageHandler;
import co.usc.net.Status;
import co.usc.net.messages.Message;
import co.usc.net.messages.StatusMessage;
import org.ethereum.core.Block;
import org.ethereum.crypto.HashUtil;

/**
 * Created by ajlopez on 5/14/2016.
 */
public class SimpleNode {
    private MessageHandler handler;
    private NodeID nodeID = new NodeID(HashUtil.randomPeerId());

    public SimpleNode(MessageHandler handler) {
        this.handler = handler;
    }

    public MessageHandler getHandler() {
        return this.handler;
    }

    public void receiveMessageFrom(SimpleNode peer, Message message) {
        SimpleNodeChannel senderToPeer = getMessageChannel(peer);
        this.handler.processMessage(senderToPeer, message);
    }

    public Block getBestBlock() {
        return ((NodeMessageHandler)handler).getBlockProcessor().getBlockchain().getBestBlock();
    }

    public BlockDifficulty getTotalDifficulty() {
        return ((NodeMessageHandler)this.handler).getBlockProcessor().getBlockchain().getTotalDifficulty();
    }

    public void sendStatusTo(SimpleNode peer) {
        Block block = this.getBestBlock();
        Status status = new Status(block.getNumber(), block.getHash().getBytes());
        peer.receiveMessageFrom(this, new StatusMessage(status));
    }

    public void sendFullStatusTo(SimpleNode peer) {
        Status status = getFullStatus();
        peer.receiveMessageFrom(this, new StatusMessage(status));
    }

    public Status getFullStatus() {
        Block block = this.getBestBlock();
        return new Status(block.getNumber(), block.getHash().getBytes(), block.getParentHash().getBytes(), this.getTotalDifficulty());
    }

    public SimpleNodeChannel getMessageChannel(SimpleNode peer) {
        return new SimpleNodeChannel(this, peer);
    }

    public NodeID getNodeID() { return nodeID; }

    public static SimpleNode createNode() {
        NodeMessageHandler handler = NodeMessageHandlerUtil.createHandler(new DummyBlockValidationRule());
        return new SimpleNode(handler);
    }
}
