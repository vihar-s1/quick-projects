#!/usr/bin/env python3
import unittest
import MorseCodec


class Test_Translator(unittest.TestCase):
    def test_EmptyString(self):
        self.assertTrue(MorseCodec.encode("") == "")

    def test_NoneType(self):
        self.assertTrue(MorseCodec.encode(None) == "")
    

    def test_encoder1(self):
        self.assertTrue(MorseCodec.encode("Hello") == ".... . .-.. .-.. --- ")

    def test_encoder2(self):
        self.assertTrue(MorseCodec.encode("hElLo") == ".... . .-.. .-.. --- ")


    def test_decoder1(self):
        self.assertTrue(MorseCodec.decode(".... . .-.. .-.. --- ") == "HELLO")
    
    def test_decoder2(self):
        self.assertTrue(MorseCodec.decode(".... . .-.. .-.. --- ! ") == "HELLO! ")
    
    def test_decoder3(self):
        self.assertTrue( MorseCodec.decode(".... . .-.. .-.. ---  .-- --- .-. .-.. -.. ") == "HELLO WORLD")


unittest.main()