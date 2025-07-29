function y_interp = P_spline (coef, x, x_interp)
	% si(x)   = ai + bi(x - xi) + ci(x - xi)^2 + di(x - xi)^3, i = 0 : n - 1
	% coef = [a0, b0, c0, d0, a1, b1, c1, d1, ..., an-1, bn-1, cn-1, dn-1]
	% x = [x0, x1, ..., xn]
	% y_interp(i) = P_spline(x_interp(i)), i = 0 : length(x_interp) - 1
	% Be careful! Indexes in Matlab start from 1, not 0

	% TODO: Calculate y_interp using the Spline coefficients
	tmp = length(x_interp);
	idxs = zeros(tmp, 1);
	for i = 1 : tmp
		% Find the interval
		idx = find(x > x_interp(i), 1);

		% Correct the interval
		if isempty(idx)
			idx = length(x) - 1;
		elseif idx == 1
			idx = 1;
		else
			idx = idx - 1;
		end

		% Store the interval
		idxs(i) = idx;
	end

	y_interp = zeros(tmp, 1);

	for i = 1 : tmp
		idx = idxs(i);
		% Calculate the value for an estetic code
		dx = x_interp(i) - x(idx);
		a = coef(4 * idx - 3);
		b = coef(4 * idx - 2);
		c = coef(4 * idx - 1);
		d = coef(4 * idx);

		% How beautiful is this formula <3
		y_interp(i) = a + b * dx + c * dx^2 + d * dx^3;
	end


end
