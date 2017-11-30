import praw
import nltk
from nltk.tokenize import RegexpTokenizer
import sys

#make bot shell here
#do majority of interesting stuff in clojure
#slurp text file generated here
#spit text file for this to use
#this opens, reads, closes file and uses text

#filepath of document full of previous replies
commentPath = '/Users/Caiti/Documents/GitHub/AverageJoe/oldComments.txt'

#filepath of document full of user comment words
wordPath = '/Users/Caiti/Documents/Github/AverageJoe/redditinput.txt'

#the instance of reddit joe is using, with bot info for signin
bot = praw.Reddit(user_agent='autoCompleteBot',
                  client_id='aAXr3ojB9PFHuQ',
                  client_secret='QGUs9LBBqgrH9EPYKKnWQDrxmOs',
                  username='MedianJoseph',
                  password='g14nt5n4k3b1rthd4yc4k3')

#this is the subreddit joe trawls, change string to change sub
#actual destination: 'fffffffuuuuuuuuuuuu'
subreddit = bot.subreddit('testingmedianjoseph')

#list of comments in sub
grabbedStuff = subreddit.stream.comments()

#the funtion joe actually uses to reply to things, and track prev replies
def reply(container, com):
    message = "Boop!"
    com.reply(message)
    container.append(com.id + '\n')
    newCommentFile = open(commentPath, 'w')
    for element in container:
        if element != '\n':
             newCommentFile.write(element)
    newCommentFile.close()
    newComments = open(commentPath, 'r').readlines()
    print('Replied to comment.')

#checks if joe has already replied to a comment
def joe_check(commnt, olde):
    for line in olde:
            if (commnt.id + '\n') == line:
                print("Found previously replied comment.")
                return True
    return False

def process_raw_comments(cList):
    newList = []
    tokenizer = RegexpTokenizer(r'\w+')
    for comment in cList:
        words = tokenizer.tokenize(comment)
        newList.append(words)
    export_words(cList)
    return newList

def export_words(wordList):
    wordFile = open(wordPath, 'w')
    for word in wordList:
        wordFile.write(word + " ")
    print("Content exported.")
    sys.exit()
    return wordList
    
def get_comments(comment):
    user = comment.author
    commentList = []
    for comment in user.comments.new(limit=None):
        commentList.append(comment.body)
    process_raw_comments(commentList)
    #can't print this, emojis confuse idle3
    return commentList

#used to decide whether to reply
def check_if_reply(comments, old):
    for comment in comments:
        if 'Boop!' in comment.body:
            if joe_check(comment, old) == True:
                print("Not replying.")
            else:
                print("Found new comment.")
                get_comments(comment)
 #               reply(old, comment)    

#gets list of already replied comments and starts the thing
def init_fn():
    print("Accessing previously replied comments.")
    commentFile = open(commentPath, 'r')
    oldComments = commentFile.readlines()
    commentFile.close()
    print("Checking for new comments.")
    check_if_reply(grabbedStuff, oldComments)

init_fn()
