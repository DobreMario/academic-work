function reduced_mat = preprocess(mat, min_reviews)
  # TODO: Remove all user rows from `mat` that have strictly less then `min_reviews` reviews.

  % This is my first attempt at computing the preprocessing.
  % It gets TIMEOUT on the test cases, on moodle. (recommendations_3/4)
  % [n, ~] = size(mat);
  % i = 1;

  % % Iterate through the rows of the matrix
  % % and remove those with less than `min_reviews` non-zero entries.
  % while i <= n
  %   if sum(mat(i, :) ~= 0) < min_reviews
  %     mat(i, :) = [];
  %     n = n - 1;
  %   else
  %     i = i + 1;
  %   end
    
  % end

  % This is the second attempt, which is much faster. (I think)
  % Compute the number of non-zero entries for each row
  reviews_per_user = sum(mat ~= 0, 2);

  % Keep only rows with at least min_reviews reviews
  reduced_mat = mat(reviews_per_user >= min_reviews, :);
end
