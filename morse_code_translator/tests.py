#!/usr/bin/env python3
import unittest
import morse_codec


class TestTranslator(unittest.TestCase):

    def test_empty_string(self):
        self.assertTrue(morse_codec.encode("") == "")

    def test_none_type(self):
        self.assertTrue(morse_codec.encode(None) == "")

    def test_encoder1(self):
        self.assertTrue(morse_codec.encode("Hello") == ".... . .-.. .-.. --- ")

    def test_encoder2(self):
        self.assertTrue(morse_codec.encode("hElLo") == ".... . .-.. .-.. --- ")

    def test_decoder1(self):
        self.assertTrue(morse_codec.decode(".... . .-.. .-.. --- ") == "HELLO")

    def test_decoder2(self):
        self.assertTrue(morse_codec.decode(".... . .-.. .-.. --- ! ") == "HELLO! ")

    def test_decoder3(self):
        self.assertTrue(
            morse_codec.decode(".... . .-.. .-.. ---  .-- --- .-. .-.. -.. ") == "HELLO WORLD"
            )


unittest.main()
