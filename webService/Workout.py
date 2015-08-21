from google.appengine.ext import ndb

class Workout(ndb.Model):
	createdBy = ndb.IntegerProperty()
	name = ndb.StringProperty()
	description = ndb.StringProperty()
	rating = ndb.IntegerProperty()
	tags = ndb.StringProperty()
	exercises = ndb.StringProperty()

	def JSONOutput(self):
		return {'workoutId': self.key.id(),
		'createdBy': self.createdBy,
		'name': self.name,
		'description': self.description,
		'tags': self.tags,
		'exercises': exercises}