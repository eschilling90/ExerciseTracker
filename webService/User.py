from google.appengine.ext import ndb

class User(ndb.Model):
	userID = ndb.IntegerProperty()
	name = ndb.StringProperty()
	permissionFlag = ndb.IntegerProperty()
	emailAddress = ndb.StringProperty()
	password = ndb.StringProperty()
