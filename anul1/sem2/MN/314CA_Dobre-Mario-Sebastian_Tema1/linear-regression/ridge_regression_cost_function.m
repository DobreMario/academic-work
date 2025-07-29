function [Error] = ridge_regression_cost_function(Theta, Y, FeatureMatrix, lambda)
  	% Theta -> the vector of weights
  	% Y -> the vector with all actual values
  	% FeatureMatrix -> the matrix with all training examples
  	% lambda -> regularization parameter that controls the amount of 
  	%           shrinkage applied to the regression coefficients

  	% Error -> the error of the regularized cost function

  	% TODO: ridge_regression_cost_function implementation
	m = length(Y);
	h_x = FeatureMatrix * Theta(2 : end);
	diff = h_x - Y;
	diff = diff .^ 2;
	Error = sum(diff) / (2 * m) + lambda * (norm(Theta, 2) ^ 2);
end
