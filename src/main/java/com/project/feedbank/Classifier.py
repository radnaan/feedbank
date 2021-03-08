import nltk
from nltk.corpus import stopwords 
from nltk.tokenize import word_tokenize, sent_tokenize
from nltk.sentiment.vader import SentimentIntensityAnalyzer

#Identifies if a text is positive, negative
#or neutral
def classify_text(text):
    sentences = sent_tokenize(text)

    if len(sentences) == 0:
        return 'unclassified'

    total_score = 0
    
    #For each sentence get score
    for sentence in sentences:
        total_score += score_sentence(sentence)

    #Calculate average score
    avg_score = total_score / len(sentences)

    #Classify as one of three
    if avg_score >= 0.05:
        return 'positive'
    elif avg_score <= -0.05:
        return 'negative'
    else:
        return 'neutral'

#Gets a score for the sentence which represents if it is
#positive, negative, or neutral
def score_sentence(sentence):
    #print('Classifying: {}'.format(sentence))
    
    analyzer = SentimentIntensityAnalyzer()
    c = analyzer.polarity_scores(sentence)
    score = c['compound']
    
    #print('Score: {}'.format(score))
    return score

def count_words(sentence):
    return len(word_tokenize(sentence))