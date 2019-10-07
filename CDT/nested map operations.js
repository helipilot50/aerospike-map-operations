/*
Unique key map bin operations. Create map operations used by the client operate command. The default unique key map is unordered.
All maps maintain an index and a rank. The index is the item offset from the start of the map, for both unordered and ordered maps. The rank is the sorted index of the value component. Map supports negative indexing for index and rank.

Index examples:

Index 0: First item in map.
Index 4: Fifth item in map.
Index -1: Last item in map.
Index -3: Third to last item in map.
Index 1 Count 2: Second and third items in map.
Index -3 Count 3: Last three items in map.
Index -5 Count 4: Range between fifth to last item to second to last item inclusive.

Rank examples:

Rank 0: Item with lowest value rank in map.
Rank 4: Fifth lowest ranked item in map.
Rank -1: Item with highest ranked value in map.
Rank -3: Item with third highest ranked value in map.
Rank 1 Count 2: Second and third lowest ranked items in map.
Rank -3 Count 3: Top three ranked items in map.

Nested CDT operations are supported by optional CTX context arguments. Examples:
*/

// example 1
const before1 = {
    key1: {
        key11: 9,
        key12: 4
    },
    key2: {
        key21: 3,
        key22: 5
    }
}

/*
Set map value to 11 for map key "key21" inside of map key "key2".
*/

MapOperation.put(MapPolicy.Default,
    "binName",
    Value.get("key21"),
    Value.get(11),
    CTX.mapKey(Value.get("key2"))
)


const after1 = {
    key1: {
        key11: 9,
        key12: 4
    },
    key2: {
        key21: 11,
        key22: 5
    }
}

// Example 2

const before2 = {
    key1: {
        key11: {
            key111: 1
        },
        key12: {
            key121: 5
        }
    },
    key2: {
        key21: {
            key211: 7
        }
    }
}

// Set map value to 11 in map key "key121" for highest ranked map("key12") inside of map key "key1".
MapOperation.put(MapPolicy.Default, 
    "binName", 
    Value.get("key121"), 
    Value.get(11), 
    CTX.mapKey(Value.get("key1")), 
    CTX.mapRank(-1)
    )

const after2 = {
    key1: {
        key11: {
            key111: 1
        },
        key12: {
            key121: 11
        }
    },
    key2: {
        key21: {
            key211: 7
        }
    }
}

// Complex example

const before3 = {
    campaign1: {
        id: "27d8b03d-2ed9-4e3f-b38f-5b0a865a1155",
        execPlan1: {
            id: "b4b0dfc6-40c6-4414-a751-4f3ddb252233",
            name: "Sprint to the finish",
            dataCube: {
                views: 235,
                clicks: 23,
                conversions: 1,
            }
        },
        execPlan2: {
            id: "34e5af5d-e744-4e26-a941-bd7802c27fc2",
            name: "Main campain execution",
            dataCube: {
                views: 13500,
                clicks: 1978,
                conversions: 245,
            }
        },
        execPlan3: {
            id: "c2e147c7-b7ef-440e-a993-91969a4fc3c1",
            name: "Market test",
            dataCube: {
                views: 235,
                clicks: 23,
                conversions: 1,
            }
        }
    }
}