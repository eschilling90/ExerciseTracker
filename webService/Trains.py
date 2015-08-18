from google.appengine.ext import ndb

class Trains(ndb.Model):
	trainerId = ndb.IntegerProperty()
	traineeId = ndb.IntegerProperty()
