from google.appengine.ext import ndb

class Weight(ndb.Model):
	weight = ndb.IntegerProperty()
	dateTime = ndb.DateTimeProperty()
