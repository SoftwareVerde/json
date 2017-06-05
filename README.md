# Json Wrapper for Java

Json Wrapper consolidates Json.OBJECT and Json.ARRAY into a single entity.
Json is intelligently either an array or an object. By invoking the
put(key, value) method, the Json object will become an Object. If
values exist within the Json object before an invocation of put(),
then the values will be indexed as stringified-integers; the index
used will be smallest unused non-negative integer.

