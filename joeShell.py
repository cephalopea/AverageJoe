import praw

#make bot shell here
#do majority of interesting stuff in clojure
#slurp text file generated here
#spit text file for this to use
#this opens, reads, closes file and uses text

#filepath of document full of previous replies
commentPath = '/Users/Caiti/Documents/averagejoe/oldComments.txt'

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
    print(newComments)
    print('Replied to comment.')

#checks if joe has already replied to a comment
def joe_check(commnt, olde):
    for line in olde:
            if (commnt.id + '\n') == line:
                print("Found previously replied comment.")
                return True
    return False

#used to decide whether to reply
def check_if_reply(comments, old):
    for comment in comments:
        if 'slowbot' in comment.body:
            #problem is here
            #this isn't quite checking correctly
            if joe_check(comment, old) == True:
                print("Not replying.")
            else:
                print("Replying.")
                reply(old, comment)

#gets list of already replied comments and starts the thing
def init_fn():
    commentFile = open(commentPath, 'r')
    oldComments = commentFile.readlines()
    print(oldComments)
    commentFile.close()
    check_if_reply(grabbedStuff, oldComments)

init_fn()
