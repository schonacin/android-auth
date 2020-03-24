package com.blue_unicorn.android_auth_lib.cbor;

import com.upokecenter.cbor.CBOREncodeOptions;
import com.upokecenter.cbor.CBORObject;
import com.upokecenter.cbor.CBORType;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CborSerializer {

    private static final CBOREncodeOptions ENCODE_OPTIONS = CBOREncodeOptions.DefaultCtap2Canonical;

    // kind of hacky but the best way to remove the null values as of yet
    private static CBORObject serializeInnerMap(CBORObject cborObject) {
        if (!cborObject.isNull() && cborObject.getType() == CBORType.Map) {
            Set<CBORObject> keysToDelete = new HashSet<>();
            for (CBORObject key: cborObject.getKeys()) {

                if(!cborObject.get(key).isNull() && cborObject.get(key).getType() == CBORType.Map) {
                    cborObject.Set(key, serializeInnerMap(cborObject.get(key)));
                }
                if (cborObject.get(key).isNull()) {
                    keysToDelete.add(key);
                }
            }
            for(CBORObject key: keysToDelete) {
                cborObject.Remove(key);
            }
        }
        if(cborObject.size() == 0) {
            return null;
        }
        return cborObject;
    }

    public static byte[] serialize(Map<Integer, Object> map) {
        CBORObject cborObject = CBORObject.NewMap();
        for (int key: map.keySet()) {
            Object innerObject = map.get(key);
            if (innerObject != null) {
                CBORObject innerCborObject = CBORObject.FromObject(innerObject);
                if (innerCborObject.getType() == CBORType.Map) {
                    innerCborObject = serializeInnerMap(innerCborObject);
                }
                if (innerCborObject != null) {
                    cborObject.Add(key, innerCborObject);
                }
            }
        }
        return cborObject.EncodeToBytes(ENCODE_OPTIONS);
    }

}
