from google.appengine.ext import ndb

class Weight(ndb.Model):
	weightId = ndb.IntegerProperty()
	weight = ndb.IntegerProperty()
	dateTime = ndb.DateTimeProperty()
