import re

'''A file which has had the preprocessor run on it.'''
class PreprocessedFile(object):
	data = None
	def __init__(self, filename, defines = None, included = None):
		if defines is None:
			defines = [('DM_VERSION', '300')] # good enough
		if included is None:
			included = []

		# TODO: canonicalization of filenames
		if filename in included:
			return
		included.append(filename)

		# For now, don't worry about sequential yadda yadda - just read the
		# entire file instantly.
		with f = file(filename, 'rb'):
			for line in f.readlines():
				data = data.append(process_line(line, defines) + '\n')
	
	def process_line(line, defines, included):
		# if it's a #define, add it to the defines list
		m = re.match('#define\s+(?P<from>\w+)(\s+(?P<to>.*?)$', line)
		if m is not None:
			defines.append(m.match('from', 'to'))
			return
		# TODO: #undef, __FILE__, #if, and all those other ones
		# but they're lame so yknow


	
	def read(self, numbytes):
		# Line numbers are doomed. TODO: fix that
