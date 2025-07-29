function [X, y] = load_dataset(path)
  	% path -> a relative path to the .mat file that must be loaded
  
  	% X, y -> the training examples (X) and their corresponding labels (y)
  
  	% TODO: load_dataset implementation
	dataset = load(path);
	% dataset is a struct holding variables from the .mat file.
	
	X = dataset.X;
	y = dataset.y;

end
