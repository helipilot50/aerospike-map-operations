# Aerospike CDT: map operations


So make sure your server is version >= 4.6 or you  just get parameter errors. Assuming we have a bin themap, whose data type is a map and contains { foo: { bar: { baz: 3 } } }
```java
Key key = new Key(params.namespace, params.set, "ctx-ex");
String binName = params.getBinName("themap");

// Delete record if it already exists.
client.delete(params.writePolicy, key);

// Set up the example data
Map<Value,Value> core = new HashMap<Value,Value>();
Map<Value,Value> inner = new HashMap<Value,Value>();
Map<Value,Value> outer = new HashMap<Value,Value>();
core.put(Value.get("baz"), Value.get(3));
inner.put(Value.get("bar"), Value.get(core));
outer.put(Value.get("foo"),  Value.get(inner));

MapPolicy mapPolicy = new MapPolicy(MapOrder.KEY_ORDERED, (MapWriteFlags.CREATE_ONLY | MapWriteFlags.NO_FAIL));
Record record = client.operate(params.writePolicy, key,
    MapOperation.putItems(mapPolicy, binName, outer),
    MapOperation.increment(MapPolicy.Default, binName, Value.get("baz"), Value.get(5), CTX.mapKey(Value.get("foo")), CTX.mapKey(Value.get("bar")))
);

record = client.get(params.policy, key);
console.info("Record: " + record);
```
