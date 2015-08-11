from google.appengine.ext import ndb

class Notification(ndb.Model):
	notificationId = ndb.IntegerProperty()
	contents = ndb.TextProperty()
	creationDate = ndb.DateTimeProperty()
	recurrenceRate = ndb.StringProperty()
	senderId = ndb.IntegerProperty()
	receiverId = ndb.IntegerProperty()