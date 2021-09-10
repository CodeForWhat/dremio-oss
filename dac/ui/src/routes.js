/*
 * Copyright (C) 2017-2019 Dremio Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { IndexRedirect, IndexRoute, Redirect, Route } from 'react-router';
import React from 'react';

import { CheckUserAuthentication, UserIsAuthenticated } from '@app/components/Auth/authWrappers';

import { ENTITY_TYPES } from '@app/constants/Constants';
import {
  explorePageExit,
  explorePageLocationChanged,
  startExplorePageListener
} from '@app/actions/explore/dataset/data';
// import Votes from '@inject/pages/AdminPage/subpages/Votes'; // To Be Removed
import EulaPage from '@inject/pages/EulaPage/EulaPage';
import PATListPage from '@inject/pages/AccountPage/personalAccessTokens/PATListPage';
import SSOLandingPage from '@inject/pages/AuthenticationPage/components/SSOLandingPage';
import { resetModuleState } from '@app/actions/modulesState';
import { exploreStateKey } from '@app/selectors/explore';
import { LOGIN_PATH, SIGNUP_PATH, SSO_LANDING_PATH } from '@app/sagas/loginLogout';
import { lazy } from '@app/components/Lazy';
import ReflectionJobsPage from '@inject/pages/JobPage/ReflectionJobsPage';
import { AdminPageRouting } from '@inject/RouteMixin.js';
import AuthenticationPage from '@inject/pages/AuthenticationPage/AuthenticationPage';
import Info from '@inject/pages/AccountPage/subpages/InfoController';

import additionalRoutes from '@inject/additionalRoutes';
import JobPage from '@inject/pages/QVJobPage/QVJobPage';

import App from './containers/App';

import ReloadPage from './pages/ReloadPage';

import HomeModals from './pages/HomePage/HomeModals';
import Home from './pages/HomePage/subpages/Home';
import { AllSpaces } from './pages/HomePage/subpages/AllSpaces/AllSpaces';
import AllSources from './pages/HomePage/subpages/AllSources/AllSources';

import ExploreModals from './pages/ExplorePage/ExploreModals';

import AccountPage from './pages/AccountPage/AccountPage';

import SignupPage from './pages/SignupPage/SignupPage';
import ServerStatusPage from './pages/ServerStatusPage/ServerStatusPage';

import AccountModals from './pages/AccountPage/AccountModals';

import JobModals from './pages/JobPage/JobModals';

import Page, { MainMasterPage } from './components/Page';

window.React = React;

const resourceKeyName = 'resourceId';
export const getSourceRoute = (rootType, component) => {
  const suffix = `/${rootType}/:${resourceKeyName}`;
  return (
    <Route path={suffix} component={component}>
      <Route path={`${suffix}/folder/**`} />
    </Route>
  );
};

const ExplorePage = lazy(() => import('./pages/ExplorePage/ExplorePageController' /* webpackChunkName: 'ExplorePage' */));
const getExploreRoute = (routeProps, dispatch) => {

  const onEnter = () => {
    dispatch(startExplorePageListener(true));
  };

  const onLeave = () => {
    // kill explore state to make sure that explore page would not be rendered with invalid state
    // DX-16117
    dispatch(resetModuleState(exploreStateKey));
    dispatch(explorePageExit());
  };

  const onChange = (prevState, newState) => {
    dispatch(explorePageLocationChanged(newState));
  };

  return (
    <Route {...routeProps}
      onEnter={onEnter}
      onLeave={onLeave}
      onChange={onChange}
    />
  );
};

export default dispatch => (
  <Route path='/' component={App}>
    {/* TODO conflict with (/:resources), need to change resources for all components */}
    <Redirect from='/home' to='/' />
    <Redirect from='/*/**/' to='/*/**' />
    <Route path='/reload' component={ReloadPage} />
    <Route path='/sso' component={SSOLandingPage} />
    <Route path={SSO_LANDING_PATH} component={SSOLandingPage} />
    <Route component={Page}>
      <Route path='/eula' component={EulaPage} />
      <Route component={CheckUserAuthentication}>
        <Route path={LOGIN_PATH} component={AuthenticationPage} />
        <Route path={SIGNUP_PATH} component={SignupPage} />
        <Route path='/status' component={ServerStatusPage} />
      </Route>
    </Route>
    {additionalRoutes}
    <Route component={CheckUserAuthentication}>
      <Route component={UserIsAuthenticated(JobModals)}>
        <Route component={Page}>
          <Route path='/jobs/reflection/:reflectionId' component={ReflectionJobsPage} />
          <Route path='/jobs(/:queryId)' component={JobPage} />
        </Route>
      </Route>
      <Route component={UserIsAuthenticated(AccountModals)}>
        <Route component={Page}>
          <Route path='/account' component={AccountPage} >
            <IndexRedirect to='/account/info' />
            <Route path='/account/info' component={Info} />
            <Route path='/account/personalTokens' component={PATListPage} />
          </Route>
        </Route>
      </Route>
      {AdminPageRouting()}
      <Route component={UserIsAuthenticated(HomeModals)}>
        <Route component={Page}>
          <IndexRoute component={Home} /> {/* todo: is this valid?*/}
          {/* a complicate route structure below is needed for correct work of Link component
          from router package for case of onlyActiveOnIndex=false */}
          {getSourceRoute(ENTITY_TYPES.source, Home)}
          {getSourceRoute(ENTITY_TYPES.space, Home)}
          <Route path='/home' component={Home}>
            <Route path={`/home/:${resourceKeyName}/folder/**`} />
          </Route>
          <Route path='/spaces/list' component={AllSpaces} />
          <Route path='/sources/list' component={AllSources} />
          <Route path='/sources/datalake/list' component={AllSources} />
          <Route path='/sources/external/list' component={AllSources} />
        </Route>
      </Route>
      <Route component={MainMasterPage}>
        {
          getExploreRoute({
            component: UserIsAuthenticated(ExploreModals),
            children: [
              <Route key='new_query' path='/new_query' component={ExplorePage} />,
              <Route key='existing_dataset' path='/:resources(/:resourceId)/:tableId(/:pageType)' component={ExplorePage} />
            ]
          }, dispatch)
        }
      </Route>
    </Route>
  </Route>
);
