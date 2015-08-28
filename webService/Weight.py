from google.appengine.ext import ndb

class Weight(ndb.Model):
	emailAddress = ndb.StringProperty()
	weight = ndb.FloatProperty()
	dateTime = ndb.DateTimeProperty(auto_now_add=True)
