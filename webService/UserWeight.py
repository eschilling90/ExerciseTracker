from google.appengine.ext import ndb

class UserWeight(ndb.Model):
	userEmail = ndb.StringProperty()
	weightId = ndb.IntegerProperty()
