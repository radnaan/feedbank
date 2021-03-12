import re

from nltk.corpus import stopwords 
from nltk.tokenize import word_tokenize, sent_tokenize

#Tries to simplify the request 
def format_request(request):
    request = request.replace('not need', 'don\'t')
    request = request.replace('different', 'change')
    words = request.split(' ')

    accepted = []

    after_would = False

    for word in words:
        if word == '':
            continue
        
        if after_would and word not in ['could','should', 'if', 'to']:
            continue
        else:
            after_would = False

        if word in ['could','should', 'if', 'to']:
            continue
        
        if word =='would':
            after_would = True
            continue
        
        if word not in ['please', 'could']:
            accepted.append(word)
    
    new_request = ' '.join(accepted)
    return new_request

#Looks for certain words to try and identify a request
def is_request(text):
    terms = [
        'please',
        'change',
        'get',
        'different',
        'remove',
        'raise',
        'lower',
        'alter',
        'could',
        'turn',
        'should',
        'would',
        'show'
        ]
    for w in terms:
        if w in text:
            return True
    return False

#Attempts to filter out non-requests
def identify_requests(text):
    simplified = simplify_text(text)

    requests = [format_request(sentence) for sentence in simplified if is_request(sentence)]
    return requests
    
#Removes common stop words from a text to make it easier to 
def simplify_text(text):
    sentences = sent_tokenize(text)

    words_to_remove = list(stopwords.words("english")) + ['\n', '', 'us']

    #Remove certain words from stopwords to retain sentence meaning
    for w in ['not', 'shouldn\'t', 'couldn\'t', 'wouldn\'t', 'should', 'if','to']:
        words_to_remove.remove(w)

    summaries = []
    
    for s in sentences:
        summaries.append(simplify_sentence(s, words_to_remove))

    return summaries

# Remove stopwords from sentences
def simplify_sentence(sentence, words_to_remove):
    doc = word_tokenize(sentence)

    #Get each word from the sentence
    words=[word.lower() for word in doc]

    chosen_words = []

    for word in words:

        if word == 'n\'t':
            chosen_words.append('not')
            continue
        
        #Remove stopwords and anything that doesn't have either a letter or a number
        if word in words_to_remove or re.findall(r'^\W*$',  word) != []:
            continue
        else:
            chosen_words.append(word)

    return " ".join(chosen_words)

def test():
    examples = fileread(filename='Test//examples.txt')

    for example in examples:
        #print(example)
        print(identify_requests(example))

def fileread(filename='examples.txt', verbose=True):
    try:
        file = open(filename, mode='r')
        lines = file.read().split('\n')
        file.close()
    except:
        if verbose:
            print('Error, could not locate file \'{}\' to read from.'.format(filename))
        return []
    return lines

if __name__ == '__main__':
    test()