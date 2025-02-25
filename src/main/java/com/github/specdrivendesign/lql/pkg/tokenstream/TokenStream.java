package com.github.specdrivendesign.lql.pkg.tokenstream;

import com.github.specdrivendesign.lql.pkg.tokens.Tokens;

public interface TokenStream {
    Tokens.Token nextToken() throws Exception;
}
