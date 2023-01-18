#!/usr/bin/env python3
import MorseCodec

message = input('Enter String: ')
encoded = MorseCodec.encode(message)
decoded = MorseCodec.decode(encoded)
print(f"encoded: {encoded} \ndecoded: {decoded}")