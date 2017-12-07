import praw
import subprocess

#filepath of document full of previous replies
commentPath = '/Users/Caiti/Documents/GitHub/AverageJoe/oldComments.txt'

#filepath of document full of user comment words
wordPath = '/Users/Caiti/Documents/GitHub/AverageJoe/redditinput.txt'

#filepath of Clojure jar for joebrains
jarPath = '/Users/Caiti/Documents/GitHub/AverageJoe/joebrains/target/uberjar/joebrains-0.1.0-SNAPSHOT-standalone.jar'

#filepath of joe's output
joePath = '/Users/Caiti/Documents/GitHub/AverageJoe/joeoutput.txt'

#the instance of reddit joe is using, with bot info for signin
bot = praw.Reddit(user_agent='autoCompleteBot',
                  client_id='aAXr3ojB9PFHuQ',
                  client_secret='QGUs9LBBqgrH9EPYKKnWQDrxmOs',
                  username='MedianJoseph',
                  password='g14nt5n4k3b1rthd4yc4k3')

oldComments = ['placeholder', 'things']

#this is the subreddit joe trawls, change string to change sub
subreddit = bot.subreddit('The_Donald')

#list of comments in sub
grabbedStuff = subreddit.stream.comments()

#gets list of already replied comments and starts the thing
def init_fn():
    print("Accessing previously replied comments.")
    commentFile = open(commentPath, 'r')
    oldComments = commentFile.readlines()
    commentFile.close()
    print("Checking for new comments.")
    check_if_reply()

#used to decide whether to reply
def check_if_reply():
    for comment in grabbedStuff:
        if 'the' in comment.body:
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

#exports words to .txt file and runs jar with clojure thing
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
    explanation = ("\n\n*****Beep boop, I'm a bot! See my source code on [GitHub!](https://github.com/cephalopea/AverageJoe)")
    replyText = ("Autocomplete: " + message[0] + explanation)
    cment.reply(replyText)
    oldComments.append(cment.author.name + '\n')
    newCommentFile = open(commentPath, 'w')
    for element in oldComments:
        newCommentFile.write(element)
    newCommentFile.close()
    newComments = open(commentPath, 'r').readlines()
    print('Replied to comment.')
    return True

#reads joe's comment file
def get_joe_reply():
    joeFile = open(joePath, 'r')
    output = joeFile.readlines()
    joeFile.close()
    return output

init_fn()

