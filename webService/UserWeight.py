from google.appengine.ext import ndb

class UserWeight(ndb.Model):
	userId = ndb.IntegerProperty()
	weightId = ndb.IntegerProperty()
