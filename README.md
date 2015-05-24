Written by Ben Wedin & Thomas Redding

We implemented the first "Challenge Feature": inferring the input type by
default

EX:

\> java EncodingHelper alphabet
String: alphabet
Codepoints: U+0061 U+006C U+0070 U+0068 U+0061 U+0062 U+0065 U+0074
UTF8: \x61\x6C\x70\x68\x61\x62\x65\x74

\> java EncodingHelper "U+0061 U+0062 U+0063"
String: abc
Codepoints: U+0061 U+0062 U+0063
UTF8: \x61\x62\x63

\> java EncodingHelper "\x78\x79\x7A"
String: xyz
Codepoints: U+0078 U+0079 U+007A
UTF8: \x78\x79\x7A

Additionally, our program accepts both lower-case and upper-case letters when
interpreting hexadecimal input such as code points and UTF-8.