/*
List operations support negative indexing. If the index is negative, the resolved index starts backwards from end of list. If an index is out of bounds, a parameter error will be returned. If a range is partially out of bounds, the valid part of the range will be returned. Index/Range examples:

Index 0: First item in list.
Index 4: Fifth item in list.
Index -1: Last item in list.
Index -3: Third to last item in list.
Index 1 Count 2: Second and third items in list.
Index -3 Count 3: Last three items in list.
Index -5 Count 4: Range between fifth to last item to second to last item inclusive.

*/

// Nested CDT operations are supported by optional CTX context arguments. Examples:

const before1 = [
    [7, 9, 5],
    [1, 2, 3],
    [6, 5, 4, 1]
]
// Append 11 to last list.
ListOperation.append("binName", Value.get(11), CTX.listIndex(-1))

const result1 = [
    [7, 9, 5],
    [1, 2, 3],
    [6, 5, 4, 1, 11]
]

const before2 = {
    key1: [
        [7, 9, 5],
        [13]
    ],
    key2: [
        [9],
        [2, 4],
        [6, 1, 9]
    ],
    key3: [
        [6, 5]
    ]
}
// Append 11 to lowest ranked list in map identified by "key2".
ListOperation.append("binName", Value.get(11), CTX.mapKey(Value.get("key2")), CTX.listRank(0))

const result2 = {
    key1: [
        [7, 9, 5],
        [13]
    ],
    key2: [
        [9],
        [2, 4, 11],
        [6, 1, 9]
    ],
    key3: [
        [6, 5]
    ]
}