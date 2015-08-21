from google.appengine.ext import ndb
from User import User
import json

class Notification(ndb.Model):
	contents = ndb.TextProperty()
	creationDate = ndb.DateTimeProperty(auto_now_add=True)
	recurrenceRate = ndb.StringProperty()
	senderId = ndb.IntegerProperty()
	receiverId = ndb.IntegerProperty()
	isRead = ndb.BooleanProperty(default=False)

	def JSONOutputShort(self):
		sender = User.get_by_id(self.senderId)
		content = json.loads(str(self.contents))
		return {'notificationId': self.key.id(),
		'title': content["title"],
		'creationDate': self.creationDate.isoformat(),
		'recurrenceRate': self.recurrenceRate,
		'senderId': sender.key.id(),
		'senderName': sender.name,
		'read': self.isRead}

	def JSONOutputDetail(self):
		sender = User.get_by_id(self.senderId)
		content = json.loads(self.contents)
		return {'notificationId': self.key.id(),
		'contents': contents,
		'creationDate': self.creationDate.isoformat(),
		'recurrenceRate': self.recurrenceRate,
		'senderId': sender.key.id(),
		'senderName': sender.name,
		'read': self.isRead}