# A-Z 0-9 , . ? / - ( )
# single space has empty string encoding

__MORSE_CODE_DICT = {
    ' ': '',
    'A': '.-', 'B': '-...', 'C': '-.-.', 'D': '-..',
    'E': '.', 'F': '..-.', 'G': '--.', 'H': '....',
    'I': '..', 'J': '.---', 'K': '-.-', 'L': '.-..',
    'M': '--', 'N': '-.', 'O': '---', 'P': '.--.',
    'Q': '--.-', 'R': '.-.', 'S': '...', 'T': '-',
    'U': '..-', 'V': '...-', 'W': '.--', 'X': '-..-',
    'Y': '-.--', 'Z': '--..', '0': '-----', '1': '.----',
    '2': '..---', '3': '...--', '4': '....-', '5': '.....',
    '6': '-....', '7': '--...', '8': '---..', '9': '----.',
    ',': '--..--', '.': '.-.-.-', '?': '..--..', '/': '-..-.',
    '-': '-....-', '(': '-.--.', ')': '-.--.-',
}


def encode(message: str | None) -> str:
    '''
    Encodes given string in morse code. Only converts characters present in the DataSet.
    '''
    # corner case for empty messages or Null strings
    if message is None or message == "":
        return ""

    # Strip space characters and convert message to all uppercase characters
    message = message.strip().upper()

    encoded_string = ""
    for letter in message:
        # Will return encoded version of the letter if letter exists in dictionary
        # If letter is not present in the dictionary, the letter itself is returned

        encoded_string += __MORSE_CODE_DICT.get(letter, letter) + " "

    return encoded_string


def decode(encoded_string: str | None) -> str:
    '''
    Decodes the given encoded string. Only focuses on valid encoded portions.
    '''

    # Strips all the extra empty spaces
    # The final blank space is to indicate decoding of the last encoded character of the string
    encoded_string = encoded_string.strip() + " "

    if encoded_string is None or encoded_string == "":
        return ""

    message = ""    # Decoded Message
    encoding = ""   # Keeps track of current dots and dashes for current letter

    for letter in encoded_string:
        # if . or -, then valid encoded character and so track it
        if letter == '.' or letter == '-':
            encoding += letter
            i = 0
        # empty space found so decode the character
        # single empty space indicates change of letter in a word
        # double empty space indicates change of a word
        elif letter == ' ':
            i += 1
            if i == 1:
                # Accessing key by using value --> from list of values, find index of value=encoding,
                # and then, using that index, access the corresponding key in the list of keys
                message += list( __MORSE_CODE_DICT.keys() )[ list(__MORSE_CODE_DICT.values()).index(encoding) ]
                encoding = ""
            elif i == 2:
                message += " "
        # if letter is neither '.' ,'-', or ' ', then it must be a character outside of
        # the morse code dictionary and hence append it back as it is
        else:
            message += letter
            i = 0

    return message
