from google.appengine.ext import ndb

class Exercise(ndb.Model):
	exerciseId = ndb.IntegerProperty()
	name = ndb.StringProperty()
	rating = ndb.IntegerProperty(default=5)
	notes = ndb.StringProperty()
	multimedia = ndb.StringProperty()
	description = ndb.StringProperty()
	tags = ndb.StringProperty()
	createdBy = ndb.IntegerProperty()
	creationDate = ndb.DateTimeProperty(auto_now_add=True)

	@staticmethod
	def generateId():
		maxId = 0
		for exercise in Exercise.query():
			if exercise.exerciseId > maxId:
				maxId = exercise.exerciseId
		return maxId + 1

	def JSONOutput(self):
		return {'exerciseId': self.exerciseId,
		'name': self.name,
		'rating': self.rating,
		'notes': self.notes,
		'multimedia': self.multimedia,
		'description': self.description,
		'tags': self.tags,
		'createdBy': self.createdBy,
		'creationDate': self.creationDate.isoformat()}