package org.sky.app.config;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/** WebMVC設定. */
@Configuration
public class WebFluxConfig implements WebFluxConfigurer {

  @Override
  public Validator getValidator() {
    return validator();
  }

  /** メッセージソース(自動設定メッセージソースの取得). */
  @Autowired private MessageSource messageSource;

  /**
   * バリデータbean登録.
   *
   * @return バリデーションメッセージもmessages.propertiesから取得する.
   */
  @Bean
  public LocalValidatorFactoryBean validator() {
    final var lvfb = new LocalValidatorFactoryBean();
    lvfb.setValidationMessageSource(messageSource);
    return lvfb;
  }

  /**
   * モデルマッパー登録.<br>
   * 条件は厳密、publicのみ、Nullでない項目をマップする。
   *
   * @return モデルマッパー
   */
  @Bean
  public ModelMapper modelMapper() {
    final var mapper = new ModelMapper();
    mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    mapper.getConfiguration().setMethodAccessLevel(AccessLevel.PUBLIC);
    mapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
    return mapper;
  }
}
