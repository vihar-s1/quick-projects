#!/usr/bin/env python3
import morse_codec

if __name__ == "__main__":
    message = input('Enter String: ')
    encoded = morse_codec.encode(message)
    decoded = morse_codec.decode(encoded)
    print(f"encoded: {encoded} \ndecoded: {decoded}")
