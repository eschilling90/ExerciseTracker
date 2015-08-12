from google.appengine.ext import ndb
from User import User
import json

class Notification(ndb.Model):
	notificationId = ndb.IntegerProperty()
	contents = ndb.TextProperty()
	creationDate = ndb.DateTimeProperty(auto_now_add=True)
	recurrenceRate = ndb.StringProperty()
	senderId = ndb.IntegerProperty()
	receiverId = ndb.IntegerProperty()
	read = ndb.BooleanProperty(default=False)

	@staticmethod
    def generateId():
        maxId = 0
        for notification in Notification.query():
            if notification.notificationId > maxId:
                maxId = notification.notificationId
        return maxId + 1

	def JSONOutputShort(self):
		sender = User.query(User.userId == self.senderId)
		content = json.load(self.contents)
		return json.dumps({'notificationId': self.notificationId,
		'title': content["title"],
		'creationDate': self.creationDate,
		'recurrenceRate': self.recurrenceRate,
		'senderId': sender.name,
		'read': self.read})

	def JSONOutputDetail(self):
		sender = User.query(User.userId == self.senderId)
		content = json.load(self.contents)
		return json.dumps({'notificationId': self.notificationId,
		'title': contents,
		'creationDate': self.creationDate,
		'recurrenceRate': self.recurrenceRate,
		'senderId': sender.name,
		'read': self.read})