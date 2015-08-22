from google.appengine.ext import ndb

class Trains(ndb.Model):
	trainerEmail = ndb.StringProperty()
	traineeEmail = ndb.StringProperty()
