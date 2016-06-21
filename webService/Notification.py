from google.appengine.ext import ndb
from User import User
import json

class Notification(ndb.Model):
	contents = ndb.TextProperty()
	creationDate = ndb.DateTimeProperty(auto_now_add=True)
	recurrenceRate = ndb.StringProperty()
	senderEmail = ndb.StringProperty()
	receiverEmail = ndb.StringProperty()
	isRead = ndb.BooleanProperty(default=False)

	def JSONOutputShort(self):
		sender = User.query(User.emailAddress==self.senderEmail).get()
		content = json.loads(str(self.contents))
		return {'notificationId': self.key.id(),
		'title': content["title"],
		'creationDate': self.creationDate.isoformat(),
		'recurrenceRate': self.recurrenceRate,
		'senderEmail': sender.emailAddress,
		'senderName': sender.name,
		'read': self.isRead}

	def JSONOutputDetail(self):
		sender = User.query(User.emailAddress==self.senderEmail).get()
		content = json.loads(self.contents)
		return {'notificationId': self.key.id(),
		'contents': content,
		'creationDate': self.creationDate.isoformat(),
		'recurrenceRate': self.recurrenceRate,
		'senderId': sender.emailAddress,
		'senderName': sender.name,
		'read': self.isRead}