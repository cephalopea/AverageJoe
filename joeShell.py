import praw
import subprocess
import sys

#filepath of document full of previous replies
commentPath = 'placeholder.txt'

#filepath of document full of user comment words
wordPath = 'placeholder.txt'

#filepath of Clojure jar for joebrains
jarPath = 'placeholder.jar'

#filepath of joe's output
joePath = 'placeholder.txt'

#the instance of reddit joe is using, with bot info for signin
bot = praw.Reddit(user_agent='placeholder',
                  client_id='placeholder',
                  client_secret='placeholder',
                  username='placeholder',
                  password='placeholder')

#gets list of reddit users the bot has already commented on
commentFile = open(commentPath, 'r')
oldComments = commentFile.readlines()
commentFile.close()

#this is the subreddit joe trawls, change string to change sub
subreddit = bot.subreddit('placeholder')

#list of comments in sub
grabbedStuff = subreddit.stream.comments()

keepLooping = True

def _init():
    keepLooping = True
    check_for_mentions()
    print("Mentions check completed.")
    userInput = input("Do you want to check for new comments fitting your criteria? ")
    if (userInput == "yes"):
        print("Checking for new comments.")
        keepLooping = False
        check_if_reply(grabbedStuff)
    else:
        print("Finishing without checking new comments.")
        sys.exit()

def check_for_mentions():
    print("Checking for mentions.")
    for message in bot.inbox.unread(limit=None):
        check_if_mention_reply(message)
        message.mark_read()

def check_if_mention_reply(mentn):
    print("Checking for new messages fitting criteria.")
    if ('/u/MedianJoseph autocomplete' in mentn.body):
        if joe_check(mentn) == True:
            print("Not replying.")
        else:
            print("Found new comment.")
            get_other_comments(mentn)
    else:
        get_operator_reply(mentn)

def get_operator_reply(mentn):
    userInput = input("Here's the message: ---" + mentn.body + "--- Type in skip to skip replying to this message, or type your reply here to write a reply. ")
    if (reply == "skip"):
        print("Skipping this message.")
    else:
        explanation = ("\n\n*****\n\nBeep boop, I'm a bot! I generate predictive text based on your comment history using genetic programming. (This comment was written by my programmer.)\n\nHave me predict your next comment by replying with /u/MedianJoseph autocomplete.\n\nCheck out my source code on [GitHub](https://github.com/cephalopea/AverageJoe)!")
        mentn.reply(userInput + explanation)
        print("Replied to comment.")

#used to decide whether to reply
def check_if_reply(container):
    print("Checking for new comments fitting criteria.")
    for comment in container:
        if ('insert search term here' in comment.body):
            if joe_check(comment) == True:
                print("Not replying.")
            else:
                print("Found new comment.")
                get_other_comments(comment)

#checks if joe has already replied to a comment
def joe_check(cment):
    for line in oldComments:
            if (cment.author.name + '\n') == line:
                print("Found previously replied comment.")
                return True
    return False 

#get selected comments' author's last 100 comments
def get_other_comments(cment):
    user = cment.author
    commentList = []
    for comment in user.comments.new(limit=100):
        commentList.append(comment.body)
    process_raw_comments(commentList, cment)
    return commentList

#processes user's comments into one long, awful list of words
def process_raw_comments(cList, cment):
    newList = []
    for comment in cList:
        words = comment.split(" ")
        for word in words:
            newList.append(word)
    export_words(newList, cment)
    return newList

#exports words to .txt file and runs .jar with clojure thing
def export_words(wordList, cment):
    wordFile = open(wordPath, 'w+')
    for word in wordList:
        wordFile.write(word)
        wordFile.write(" ")
    wordFile.close()
    print("Content exported.")
    print("Starting .jar file.")
    subp = subprocess.Popen(['java', '-jar', jarPath])
    subp.wait()
    reply(cment)
    return wordList 

#the funtion joe actually uses to reply to things, and track prev replies
def reply(cment):
    message = get_joe_reply()
    userInput = input("Here's what I've come up with: " + message[0] + ". Should I reply with that? ")
    if userInput == "yes":
        explanation = ("\n\n*****\n\nBeep boop, I'm a bot! I generate predictive text based on your comment history using genetic programming.\n\nHave me predict your next comment by replying with /u/MedianJoseph autocomplete.\n\nCheck out my source code on [GitHub](https://github.com/cephalopea/AverageJoe)!")
        replyText = ("Here's my best guess at what you'd say next: *" + message[0] + "*." + explanation)
        cment.reply(replyText)
        oldComments.append(cment.author.name + '\n')
        newCommentFile = open(commentPath, 'w')
        for element in oldComments:
            newCommentFile.write(element)
        newCommentFile.close()
        newComments = open(commentPath, 'r').readlines()
        print('Replied to comment.')
        if keepLooping == False:
            sys.exit()
        else:
            print("Starting reply process for next mention.")
    else:
        if keepLooping == False:
            print("Okay. Finishing without replying.")
            sys.exit()
        else:
            print("Attempting to respond to user mention again.")
            get_other_comments(cment)
    return True

#reads joe's comment file
def get_joe_reply():
    joeFile = open(joePath, 'r')
    output = joeFile.readlines()
    joeFile.close()
    return output

_init()

