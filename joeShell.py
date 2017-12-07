import praw
import subprocess

#make bot shell here
#do majority of interesting stuff in clojure
#slurp text file generated here
#spit text file for this to use
#this opens, reads, closes file and uses text

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

#this is the subreddit joe trawls, change string to change sub
#this sub is just a private one with me and the bot
#probably change this before turning it in
#so Lee isn't staring at your reddit history
subreddit = bot.subreddit('testingmedianjoseph')

#list of comments in sub
grabbedStuff = subreddit.stream.comments()

#reads joe's comment file
def get_joe_reply():
    joeFile = open(joePath, 'r')
    output = joeFile.readlines()
    joeFile.close()
    return output

#the funtion joe actually uses to reply to things, and track prev replies
def reply(container, com):
    message = get_joe_reply()
    explanation = ("\n\n*****Beep boop, I'm a bot! See my source code on [GitHub!](https://github.com/cephalopea/AverageJoe)")
    replyText = ("Autocomplete: " + message + explanation)
    com.reply(replyText)
    container.append(com.author.name + '\n')
    newCommentFile = open(commentPath, 'w')
    for element in container:
        newCommentFile.write(element)
    newCommentFile.close()
    newComments = open(commentPath, 'r').readlines()
    print('Replied to comment.')
    return True

#checks if joe has already replied to a comment
def joe_check(commnt, olde):
    for line in olde:
            if (commnt.author.name + '\n') == line:
                print("Found previously replied comment.")
                return True
    return False
                
def process_raw_comments(cList):
    newList = []
    for comment in cList:
        words = comment.split(" ")
        for word in words:
            newList.append(word)
    export_words(newList)
    return newList

def export_words(wordList):
    wordFile = open(wordPath, 'w+')
    for word in wordList:
        wordFile.write(word)
        wordFile.write(" ")
    wordFile.close()
    print("Content exported.")
    print("Starting .jar file.")
    subp = subprocess.Popen(['java', '-jar', jarPath])
    subp.wait()
    reply()
    return wordList
    
def get_comments(comment):
    user = comment.author
    commentList = []
    for comment in user.comments.new(limit=100):
        commentList.append(comment.body)
    process_raw_comments(commentList)
    return commentList

#used to decide whether to reply
def check_if_reply(comments, old):
    for comment in comments:
        if 'dog' in comment.body:
            if joe_check(comment, old) == True:
                print("Not replying.")
            else:
                print("Found new comment.")
                get_comments(comment)    

#gets list of already replied comments and starts the thing
def init_fn():
    print("Accessing previously replied comments.")
    commentFile = open(commentPath, 'r')
    oldComments = commentFile.readlines()
    commentFile.close()
    print("Checking for new comments.")
    check_if_reply(grabbedStuff, oldComments)

init_fn()

