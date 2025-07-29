function [Theta] = gradient_descent(FeatureMatrix, Y, n, m, alpha, iter)
  	% FeatureMatrix -> the matrix with all training examples
  	% Y -> the vector with all actual values
  	% n -> the number of predictors
  	% m -> the number of trainings
  	% alpha -> the learning rate
  	% iter -> the number of iterations

  	% Theta -> the vector of weights

  	% TODO: gradient_descent implementation

	Theta = zeros(n, 1);

	for i = 1 : iter
		h_x = FeatureMatrix * Theta;
		diff = h_x - Y;
		J = FeatureMatrix' * diff / m;
		Theta = Theta - alpha * J;
	end

end
