function [Error] = linear_regression_cost_function(Theta, Y, FeatureMatrix)
  	% Theta -> the vector of weights
  	% Y -> the vector with all actual values
  	% FeatureMatrix -> the matrix with all training examples
  	% Error -> the error of the regularized cost function
	m = length(Y);
	h_x = FeatureMatrix * Theta(2 : end);
	diff = h_x - Y;
	diff = diff .^ 2;
	Error = sum(diff) / (2 * m);
end
