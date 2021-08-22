// package org.sky.app.config;

// import org.sky.app.common.auth.AuthService;
// import org.sky.app.common.auth.AzureCertificateFilter;
// import org.sky.app.common.auth.LoginUser;
// import org.sky.app.common.auth.UserFunction;
// import java.util.function.IntFunction;
// import javax.servlet.http.HttpServletResponse;
// import javax.servlet.http.HttpSession;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.CredentialsExpiredException;
// import
// org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
// import
// org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.builders.WebSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import
// org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
// import org.springframework.security.crypto.factory.PasswordEncoderFactories;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.web.DefaultRedirectStrategy;
// import org.springframework.security.web.RedirectStrategy;
// import org.springframework.security.web.WebAttributes;
// import org.springframework.security.web.access.AccessDeniedHandler;
// import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
// import org.springframework.security.web.authentication.AuthenticationFailureHandler;
// import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
// import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
// import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

// /**
//  * セキュリティ設定.
//  *
//  * @author syst-sekiya
//  */
// @Configuration
// @EnableWebSecurity
// @EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
// @Slf4j
// public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

//   /** 日数を秒数に変換する処理. */
//   private static final IntFunction<Integer> DaysToSeconds = days -> 60 * 60 * 24 * days;

//   /** サービス. */
//   @Autowired private AuthService svc;

//   /** 証明書. */
//   @Autowired private CaCertBean cert;

//   /** 認可マネージャ. */
//   @Bean
//   @Override
//   public AuthenticationManager authenticationManager() throws Exception {
//     return super.authenticationManager();
//   }

//   /**
//    * パスワードエンコーダbean登録.
//    *
//    * @return パスワードエンコーダ
//    */
//   @Bean
//   public PasswordEncoder passwordEncoder() {
//     return PasswordEncoderFactories.createDelegatingPasswordEncoder();
//   }

//   /** WEBの設定. */
//   @Override
//   public void configure(final WebSecurity web) throws Exception {
//     // webjars、imagesなどを認証不要でアクセス可能とする。
//     web.ignoring().antMatchers("/webjars/**", "/images/**", "/css/**", "/js/**", "/favicon.ico");
//   }

//   /** 認証処理. */
//   @Override
//   protected void configure(final HttpSecurity http) throws Exception {
//     http
//         // ★リクエスト認可設定
//         // .authorizeRequests().mvcMatchers("/*").permitAll().anyRequest().authenticated();
//         .authorizeRequests()
//         .mvcMatchers("AO010", "actuator/health")
//         .permitAll() // login,login-errorはログインなしで無条件許可
//         .mvcMatchers("/pwdChg")
//         .permitAll() // パスワード変更も許可
//         .anyRequest()
//         .authenticated() // それ以外のリクエストは認証を要求
//         // ★アクセス拒否設定
//         .and()
//         .exceptionHandling()
//         .accessDeniedPage("/error") // リダイレクト先URL
//         .accessDeniedHandler(accessDeniedHandler) // ハンドラ
//         // ★ログイン設定
//         .and()
//         .formLogin()
//         .loginPage("/AO010") // ログインページ
//         .loginProcessingUrl("/sign_in") // ログイン処理URL
//         .usernameParameter("emailAddress") // ユーザー名パラメータ
//         .passwordParameter("pwd") // パスワードパラメータ
//         .successHandler(successHandler) // ログイン成功ハンドラ
//         // .defaultSuccessUrl("/AO001", true) // ログイン成功時遷移先URL（url, alwaysUse）
//         // .failureUrl("/AO010?error").permitAll() // ログイン失敗時URL
//         .failureHandler(authenticationFailureHandler) // ログイン失敗ハンドラ
//         // ★ログアウト設定
//         .and()
//         .logout()
//         .logoutUrl("/logout") // 遷移先URL
//         .logoutSuccessHandler(logoutSuccessHandler) // ログアウト成功ハンドラ
//         .invalidateHttpSession(false) // ログアウト後セッションを無効にしない
//         .deleteCookies("JSESSIONID", "XSRF-TOKEN")
//         .permitAll() // クッキーを削除する
//         // ★CSRF設定
//         .and()
//         .csrf()
//         .csrfTokenRepository(new CookieCsrfTokenRepository()) // CSRFトークンリポジトリ
//         // ★ログイン記憶設定
//         .and()
//         .rememberMe()
//         .alwaysRemember(false) // 常に記憶するかどうか。falseにすると画面チェックボックスで制御可能になる。
//         .rememberMeParameter("remember-me") // 画面からのパラメータ名
//         .useSecureCookie(true) // HTTPSのみ記憶を有効にする。
//         .rememberMeCookieName("SYST_SECURE_REM") // ログイン記憶用のCookie名
//         .tokenValiditySeconds(DaysToSeconds.apply(3)) // 記憶用に発行したCookieの有効時間(秒)
//         .key("systena") // 署名用ハッシュキー(無指定だとサーバ再起動時にSecuRandom再生成される。その場合それ以前の記憶情報が全部無効になる。)
//         // ★セッション管理設定
//         .and()
//         .sessionManagement()
//         .sessionFixation()
//         .changeSessionId() // セッションID振り直し
//         .invalidSessionUrl("/error") // セッション不正時遷移先URL
//         .maximumSessions(1) // ユーザーあたりの最大セッション数
//         .maxSessionsPreventsLogin(false) // 最大セッション時エラー
//         .expiredUrl("/error") // セッション期限切れ時遷移先URL
//         .and()
//         .and()
//         .headers()
//         .contentSecurityPolicy("script-src 'self'")
//         .and()
//         .and()
//         .addFilterAfter(new AzureCertificateFilter(cert), FilterSecurityInterceptor.class);
//   }

//   /** 認可処理. */
//   @Override
//   protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
//     auth.eraseCredentials(true) // 認証後COM001LoginUserのパスワードはクリアする。
//         .userDetailsService(svc) // 認証サービス
//         .passwordEncoder(passwordEncoder()); // パスワードエンコーダ
//   }

//   /** 認証成功ハンドラ. */
//   private final AuthenticationSuccessHandler successHandler =
//       (req, res, auth) -> {
//         final HttpSession session = req.getSession(false);
//         session.setAttribute(
//             "lastLogin", ((LoginUser) auth.getPrincipal()).getUser().getLastLoginDatetime());
//         res.sendRedirect("/AO010");
//       };

//   /** 認証失敗ハンドラ. */
//   private static final AuthenticationFailureHandler authenticationFailureHandler =
//       (req, res, auth) -> {
//         final HttpSession session = req.getSession(false);
//         if (session != null) {
//           req.getSession().setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, auth);
//         }
//         final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
//         if (auth instanceof CredentialsExpiredException) {
//           req.getSession().setAttribute("login_user", req.getParameterValues("emailAddress")[0]);
//         }
//         redirectStrategy.sendRedirect(req, res, "/AO010?error");
//       };

//   /** アクセス拒否ハンドラ. */
//   private static final AccessDeniedHandler accessDeniedHandler =
//       (req, res, e) -> {
//         log.warn(
//             "User:{} attempted to access the protected URL:{}",
//             UserFunction.Auth.get().getName(),
//             req.getRequestURI());
//         res.sendError(HttpServletResponse.SC_FORBIDDEN, "不正な操作を受け付けました。");
//       };

//   /** ログアウト成功ハンドラ. */
//   private static final LogoutSuccessHandler logoutSuccessHandler =
//       (req, res, auth) -> {
//         log.debug("logoutSuccessHandler req:{}", req.getRequestURI());
//         if (res.isCommitted()) {
//           log.debug("Response has already been committed. Unable to redirect to ");
//           return;
//         }
//         if (req.isRequestedSessionIdValid()) {
//           log.debug("requestedSessionIdValid session id:{}", req.getRequestedSessionId());
//           req.changeSessionId();
//         }
//         final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
//         redirectStrategy.sendRedirect(req, res, "/AO010");
//       };
// }
