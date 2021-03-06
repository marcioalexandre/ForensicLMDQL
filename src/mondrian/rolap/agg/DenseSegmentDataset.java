/*
// $Id: //open/mondrian-release/3.0/src/main/mondrian/rolap/agg/DenseSegmentDataset.java#2 $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2002-2002 Kana Software, Inc.
// Copyright (C) 2002-2007 Julian Hyde and others
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
//
// jhyde, 21 March, 2002
*/
package mondrian.rolap.agg;

import java.util.Iterator;
import java.util.Map;

import mondrian.rolap.CellKey;

/**
 * A <code>DenseSegmentDataset</code> is a means of storing segment values
 * which is suitable when most of the combinations of keys have a value
 * present.
 *
 * <p>The storage requirements are as follows. Table requires 1 word per
 * cell.</p>
 *
 * @author jhyde
 * @since 21 March, 2002
 * @version $Id: //open/mondrian-release/3.0/src/main/mondrian/rolap/agg/DenseSegmentDataset.java#2 $
 */
class DenseSegmentDataset implements SegmentDataset {
    private final Segment segment;
    private final Object[] values; // length == m[0] * ... * m[axes.length-1]

    DenseSegmentDataset(Segment segment, Object[] values) {
        this.segment = segment;
        this.values = values;
    }

    public Object get(CellKey key) {
        int offset = getOffset(key.getOrdinals());
        return values[offset];
    }

    public double getBytes() {
        // assume a slot, key, and value are each 4 bytes
        return values.length * 12;
    }

    public void put(CellKey key, Object value) {
        int offset = getOffset(key.getOrdinals());
        values[offset] = value;
    }

    public Iterator<Map.Entry<CellKey, Object>> iterator() {
        return new Itr();
    }

    boolean contains(Object[] keys) {
        return getOffset(keys) >= 0;
    }

    Object get(Object[] keys) {
        int offset = getOffset(keys);
        return keys[offset];
    }

    void put(Object[] keys, Object value) {
        int offset = getOffset(keys);
        keys[offset] = value;
    }

    private int getOffset(int[] keys) {
        int offset = 0;
        for (int i = 0; i < keys.length; i++) {
            Aggregation.Axis axis = segment.axes[i];
            Object[] ks = axis.getKeys();
            offset *= ks.length;
            offset += keys[i];
        }
        return offset;
    }

    private int getOffset(Object[] keys) {
        int offset = 0;
outer:
        for (int i = 0; i < keys.length; i++) {
            Aggregation.Axis axis = segment.axes[i];
            Object[] ks = axis.getKeys();
            offset *= ks.length;
            Object value = keys[i];
            for (int j = 0, axisLength = ks.length; j < axisLength; j++) {
                if (ks[j].equals(value)) {
                    offset += j;
                    continue outer;
                }
            }
            return -1; // not found
        }
        return offset;
    }

    void set(int k, Object o) {
        values[k] = o;
    }

    /**
     * Iterator over a DenseSegmentDataset.
     *
     * <p>This is a 'cheap' implementation
     * which doesn't allocate a new Entry every step: it just returns itself.
     * The Entry must therefore be used immediately, before calling
     * {@link #next()} again.
     */
    private class Itr implements
        Iterator<Map.Entry<CellKey, Object>>,
        Map.Entry<CellKey, Object>
    {
        private int i = -1;
        private final int[] ordinals;
        private final CellKey key;

        Itr() {
            ordinals = new int[segment.axes.length];
            ordinals[ordinals.length - 1] = -1;
            key = CellKey.Generator.newRefCellKey(ordinals);
        }

        public boolean hasNext() {
            return i < values.length - 1;
        }

        public Map.Entry<CellKey, Object> next() {
            ++i;
            int k = ordinals.length - 1;
            while (k >= 0) {
                if (ordinals[k] < segment.axes[k].getKeys().length - 1) {
                    ++ordinals[k];
                    break;
                } else {
                    ordinals[k] = 0;
                    --k;
                }
            }
            return this;
        }

        // implement Iterator
        public void remove() {
            throw new UnsupportedOperationException();
        }

        // implement Entry
        public CellKey getKey() {
            return key;
        }

        // implement Entry
        public Object getValue() {
            return values[i];
        }

        // implement Entry
        public Object setValue(Object value) {
            throw new UnsupportedOperationException();
        }
    }
}

// End DenseSegmentDataset.java
