function similarity = cosine_similarity(A, B)
  # TODO: Compute the cosine similarity between two column vectors.

  % tmp1 is the dot product of A and B
  tmp1 = B' * A;

  % tmp2 is the product of the norms of A and B
  tmp2 = norm(A) * norm(B);

  % Compute the cosine similarity
  if tmp2 == 0
    similarity = 0;
  else
    similarity = tmp1 / tmp2;
  end
end
