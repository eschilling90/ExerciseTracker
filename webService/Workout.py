from google.appengine.ext import ndb

class Workout(ndb.Model):
	workoutId = ndb.IntegerProperty()
	name = ndb.StringProperty()
	description = ndb.StringProperty()
	rating = ndb.IntegerProperty()
	tags = ndb.StringProperty()
