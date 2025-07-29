function [Theta] = normal_equation(FeatureMatrix, Y, tol, iter)
  	% FeatureMatrix -> the matrix with all training examples
  	% Y -> the vector with all actual values
  	% tol -> the learning rate
  	% iter -> the number of iterations
  	% tol -> the tolerance level for convergence of the conjugate gradient method

  	% Theta -> the vector of weights
  
  	% TODO: normal_equation implementation

	[m, n] = size(FeatureMatrix);
	Theta = zeros(n + 1, 1);

	A = FeatureMatrix' * FeatureMatrix;
	b = FeatureMatrix' * Y;
	[~, flag] = chol(A);
	if flag ~= 0
		return; % A is not positive definite
	end

        %Conjugate Gradient Method imported from lab5

	x = Theta(2 : n + 1); 
        r = b;
        p = r;
        i = 0;
        while i < iter
                rr = (r' * r);
                ap = A * p;
                pap = p' * A * p;
                a = rr / pap;
                x = x + a * p;
                r = r - a * ap;
                if norm(r, inf) < tol
                        break;
                end
                beta = (r' * r) / rr;
                p = r + beta * p;
        i = i + 1;
        end
        Theta(2 : n + 1) = x;
end
