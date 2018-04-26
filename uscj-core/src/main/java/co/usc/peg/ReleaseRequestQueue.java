/*
 * This file is part of RskJ
 * Copyright (C) 2017 RSK Labs Ltd.
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

package co.usc.peg;

import co.usc.ulordj.core.Address;
import co.usc.ulordj.core.Coin;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

/**
 * Representation of a queue of btc release
 * requests waiting to be processed by the bridge.
 *
 * @author Ariel Mendelzon
 */
public class ReleaseRequestQueue {
    public static class Entry {
        private Address destination;
        private Coin amount;

        public Entry(Address destination, Coin amount) {
            this.destination = destination;
            this.amount = amount;
        }

        public Address getDestination() {
            return destination;
        }

        public Coin getAmount() {
            return amount;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }

            Entry otherEntry = (Entry) o;

            return otherEntry.getDestination().equals(getDestination()) &&
                    otherEntry.getAmount().equals(getAmount());
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.getDestination(), this.getAmount());
        }
    }

    public interface Processor {
        boolean process(Entry entry);
    }

    private List<Entry> entries;

    public ReleaseRequestQueue(List<Entry> entries) {
        this.entries = new ArrayList<>(entries);
    }

    public List<Entry> getEntries() {
        return new ArrayList<>(entries);
    }

    public void add(Address destination, Coin amount) {
        entries.add(new Entry(destination, amount));
    }

    /**
     * This methods iterates the requests in the queue
     * and calls the processor for each. If the
     * processor returns true, then the item is removed
     * (i.e., processing was successful). Otherwise it is
     * sent to the back of the queue for future processing.
     */
    public void process(int maxIterations, Processor processor) {
        ListIterator<Entry> iterator = entries.listIterator();
        List<Entry> toRetry = new ArrayList<>();
        int i = 0;
        while (iterator.hasNext() && i < maxIterations) {
            Entry entry = iterator.next();
            iterator.remove();
            ++i;

            if (!processor.process(entry)) {
                toRetry.add(entry);
            }
        }

        entries.addAll(toRetry);
    }
}
