function [J, grad] = cost_function(params, X, y, lambda, ...
	input_layer_size, hidden_layer_size, ...
	output_layer_size)

% params -> vector containing the weights from the two matrices
%           Theta1 and Theta2 in an unrolled form (as a column vector)
% X -> the feature matrix containing the training examples
% y -> a vector containing the labels (from 1 to 10) for each
%      training example
% lambda -> the regularization constant/parameter
% [input|hidden|output]_layer_size -> the sizes of the three layers

% J -> the cost function for the current parameters
% grad -> a column vector with the same length as params
% These will be used for optimization using fmincg

% TODO: cost_function implementation

m = size(X, 1);
y_OHE = (eye(output_layer_size)(y, :))'; % One-Hot Encoding of y + y_OHE = y_OHE'
J = 0;
grad = zeros(size(params));

% TODO1: get Theta1 and Theta2 (from params). Hint: reshape

Theta1 = reshape(params(1 : hidden_layer_size * (input_layer_size + 1)), ...
	  hidden_layer_size, (input_layer_size + 1));

Theta2 = reshape(params((1 + (hidden_layer_size * (input_layer_size + 1))) : end), ...
	  output_layer_size, (hidden_layer_size + 1));

% TODO2: Forward propagation

a1 = [ones(m, 1) X];
a1 = a1';
z2 = Theta1 * a1;
a2 = sigmoid(z2);
a2 = [ones(1, m); a2];
z3 = Theta2 * a2;
a3 = sigmoid(z3);
% TODO3: Compute the error in the output layer and perform backpropagation

delta3 = a3 - y_OHE; % We use a One-Hot matrix to represent labels
		  % in a format compatible with the network's outputs, 
		   % enabling error calculation between the output
		  % activations and the true labels.

Delta2 = delta3 * a2';
delta2 = ((Theta2(:, 2 : end)') * delta3) .* (sigmoid(z2) .* (1 - sigmoid(z2)));
Delta1 = delta2 * a1';

% TODO4: Determine the gradients

Theta1_grad = (1 / m) * Delta1;
Theta2_grad = (1 / m) * Delta2;

Theta1_grad(:, 2:end) = Theta1_grad(:, 2:end) + (lambda / m) * Theta1(:, 2:end);
Theta2_grad(:, 2:end) = Theta2_grad(:, 2:end) + (lambda / m) * Theta2(:, 2:end);

% TODO5: Final J and grad

grad = [Theta1_grad(:); Theta2_grad(:)];

reg_num = (lambda / (2 * m)) * (sum(sum(Theta1(:, 2 : end) .^ 2)) + sum(sum(Theta2(:, 2 : end) .^ 2)));
J = (1 / m) * sum(sum(-y_OHE .* log(a3) - (1 - y_OHE) .* log(1 - a3))) + reg_num;
% a3 represents the network's predictions for each example (h_theta(x)),
% i.e., the probabilities of each example belonging to a particular class.

end
