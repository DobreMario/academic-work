function [X_train, y_train, X_test, y_test] = split_dataset(X, y, percent)
	% X -> the loaded dataset with all training examples
	% y -> the corresponding labels
	% percent -> fraction of training examples to be put in training dataset
    
	% X_[train|test] -> the datasets for training and test respectively
	% y_[train|test] -> the corresponding labels
    
	% Example: [X, y] has 1000 training examples with labels and percent = 0.85
	%           -> X_train will have 850 examples
	%           -> X_test will have the other 150 examples

	% TODO: split_dataset implementation

	[n, ~] = size(X);
	random_idx = randperm(n); % obtain a random permutation of n elements

	training_num = round(n * percent);

	train_idx = random_idx(1 : training_num);
	test_idx = random_idx(training_num + 1 : n);

	X_train = X(train_idx, :);
	y_train = y(train_idx);
	X_test = X(test_idx, :);
	y_test = y(test_idx);

end
