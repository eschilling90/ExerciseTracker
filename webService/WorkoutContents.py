from google.appengine.ext import ndb

class WorkoutContents(ndb.Model):
	workoutId = ndb.IntegerProperty()
	exerciseId = ndb.IntegerProperty()
