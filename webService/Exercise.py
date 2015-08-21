from google.appengine.ext import ndb

class Exercise(ndb.Model):
	name = ndb.StringProperty()
	rating = ndb.IntegerProperty(default=5)
	notes = ndb.StringProperty()
	multimedia = ndb.StringProperty()
	description = ndb.StringProperty()
	tags = ndb.StringProperty()
	createdBy = ndb.IntegerProperty()
	creationDate = ndb.DateTimeProperty(auto_now_add=True)

	def JSONOutput(self):
		return {'exerciseId': self.key.id(),
		'name': self.name,
		'rating': self.rating,
		'notes': self.notes,
		'multimedia': self.multimedia,
		'description': self.description,
		'tags': self.tags,
		'createdBy': self.createdBy,
		'creationDate': self.creationDate.isoformat()}