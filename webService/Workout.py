from google.appengine.ext import ndb

class Workout(ndb.Model):
	workoutId = ndb.IntegerProperty()
	createdBy = ndb.IntegerProperty()
	name = ndb.StringProperty()
	description = ndb.StringProperty()
	rating = ndb.IntegerProperty()
	tags = ndb.StringProperty()
	exercises = ndb.StringProperty()

	@staticmethod
	def generateId():
		maxId = 0
		for workout in Workout.query():
			if workout.workoutId > maxId:
				maxId = workout.workoutId
		return maxId + 1

	def JSONOutput(self):
		return {'workoutId': self.workout,
		'createdBy': self.createdBy,
		'name': self.name,
		'description': self.description,
		'tags': self.tags,
		'exercises': exercises}