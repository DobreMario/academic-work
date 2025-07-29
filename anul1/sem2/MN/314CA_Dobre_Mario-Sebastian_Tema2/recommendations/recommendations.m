function recoms = recommendations(path, liked_theme, num_recoms, min_reviews, num_features)
  # TODO: Get the best `num_recoms` recommandations similar with 'liked_theme'.

  % Load the data, preprocess it, and compute the recommendations.
  mat = read_mat(path);
  mat = preprocess(mat, min_reviews);

  % Compute the SVD of the matrix and find the most similar themes.
  [~, ~, V] = svds(mat, num_features);
  V = V';
  [~, n] = size(V);

  % This is my first attempt at computing the cosine similarity.
  % But it get TIMEOUT on the test cases, on moodle.
  % similarity = zeros(1, num_features);
  % for i = 1 : n
  %   if i ~= liked_theme
  %     similarity(i) = cosine_similarity(V(:, i), V(:, liked_theme));
  %   end
  % end

  % This is the second attempt, which is much faster. (I think)

  v_liked = V(:, liked_theme);
  similarity = ((V' * v_liked) ./ (vecnorm(V)' * norm(v_liked)))';
  similarity(liked_theme) = 0;

  % Sort the similarities and get the top recommendations.
  [~, indices] = sort(similarity, 'descend');
  recoms = indices(1:num_recoms);

end

% NOTE: The 'preprocess' function was too slow for the test cases.
%       It was replaced with a faster version.
%       It takes 10 seconds to run to preprocess the data.
%       Now it takes less than 1 second.
